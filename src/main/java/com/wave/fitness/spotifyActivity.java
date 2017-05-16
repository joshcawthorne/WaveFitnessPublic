package com.wave.fitness;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import com.spotify.sdk.android.player.Metadata;
import com.squareup.otto.Subscribe;
import com.wave.fitness.fragments.MapViewFragment;
import com.wave.fitness.fragments.PedometerFragment;
import com.wave.fitness.fragments.SpotifyFragmentActivity;
import com.wave.fitness.runningEvent.EndRunEvent;
import com.wave.fitness.runningEvent.LocationChangedEvent;
import com.wave.fitness.runningEvent.StartRunEvent;
import com.wave.fitness.runningEvent.TrackChangedEvent;
import com.wave.fitness.runningEvent.UpdateRunStatEvent;

public class spotifyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SpotifyFragmentActivity spot;
    private FloatingActionButton btn;
    private SpotifyCore core;

    //Fragment fragment = new genreFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        core = (SpotifyCore) getApplicationContext();
        core.isRunning = false;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn = (FloatingActionButton) findViewById(R.id.toggleRunButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(core.isRunning){
                    final ProgressDialog progressDialog = new ProgressDialog(spotifyActivity.this,
                            R.style.DialogBox);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Saving your run...");
                    progressDialog.show();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onEndRun();
                            core.isRunning = false;
                        }
                    }, 2000);
                }else {
                    onStartRun();
                    core.isRunning = true;
                }

            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        String intentFragment = getIntent().getStringExtra("frgToLoad");

        if(intentFragment == "FRAGMENT_A") {
            viewPager.setCurrentItem(1, true);
        }


        else if(intentFragment == "FRAGMENT_B") {
            Log.e("Moving", "True");
            MoveNext();
        }


        else if(intentFragment == "FRAGMENT_C") {
            viewPager.setCurrentItem(3, true);
        }

    }

    @Override public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(spot = new SpotifyFragmentActivity(), "Music");
        adapter.addFragment(new MapViewFragment(), "Running");
        adapter.addFragment(new PedometerFragment(), "Stats");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void MoveNext() {
        Log.e("MoveState", "Moving");
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    public void MovePrevious() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(spotifyActivity.this);
        builder.setMessage("You're mid-run, if you quit now you'll lose your progress!").setPositiveButton("Quit", dialogClickListener)
                .setNegativeButton("Don't quit", dialogClickListener).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(spotifyActivity.this);
                builder.setMessage("You're mid-run, if you quit now you'll lose your progress!").setPositiveButton("Quit", dialogClickListener)
                        .setNegativeButton("Don't quit", dialogClickListener).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
            }
        }
    };

    //Data keeping
    private int _StepValue = 0;
    private int _AvrPaceValue = 0;
    private long _startRunTime;
    private float _DistanceValue;
    private float _AvrSpeedValue = 0;
    private int _CaloriesValue;
    private ArrayList<Location> route;
    public ArrayList<Metadata.Track> songs = new ArrayList<>();

    @Subscribe
    public void onLocationChanged(LocationChangedEvent event){
        route = event.route;
    }
    @Subscribe
    public void onUpdateRunStat(UpdateRunStatEvent event){
        if(_StepValue == 0){
            _StepValue = event.mStepValue;
        }else{
            _StepValue = (_StepValue + event.mStepValue)/2;
        }
        if (_AvrPaceValue == 0){
            _AvrPaceValue = event.mPaceValue;
        }else{
            _AvrPaceValue = (_AvrPaceValue + event.mPaceValue)/2;
        }
        _DistanceValue = event.mDistanceValue;
        if(_AvrSpeedValue == 0){
            _AvrSpeedValue = event.mSpeedValue;
        }else{
            _AvrSpeedValue = (_AvrSpeedValue+event.mSpeedValue)/2;
        }
        _CaloriesValue = event.mCaloriesValue;
    }
    @Subscribe
    public void onTrackChanged(TrackChangedEvent event){
        SpotifyCore core = (SpotifyCore) getApplicationContext();
        songs.add(core.mMetadata.currentTrack);
    }

    private void onStartRun(){
        _startRunTime = System.currentTimeMillis();
        BusProvider.getInstance().post(new StartRunEvent());
    }

    private void onEndRun(){
        BusProvider.getInstance().post(new EndRunEvent());

        SharedPreferences prefs = getSharedPreferences("com.wave.fitness", MODE_PRIVATE);
        int runId = prefs.getInt("runID", 0)+1;
        Repo_RunStatistic repo = new Repo_RunStatistic(getApplicationContext());
        Data_RunStatistic data = new Data_RunStatistic();

        data.id = runId;
        data.date = System.currentTimeMillis();
        data.duration = System.currentTimeMillis() - _startRunTime;
        data.avrspeed = _AvrSpeedValue;
        data.avrpace = _AvrPaceValue;
        data.distance = _DistanceValue;
        data.calories = _CaloriesValue;
        data.totalStep = _StepValue;
        data.route = route;
        data.songs = songs;

        repo.insert(data);

        Intent postRun = new Intent(this, PostRunActivity.class);
        postRun.putExtra("runID", runId);
        prefs.edit().putInt("runID", runId).apply(); //Update RunID in SharedPrefs
        startActivity(postRun);
        this.finish();
    }
}
