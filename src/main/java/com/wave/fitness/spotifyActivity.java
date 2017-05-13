package com.wave.fitness;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.wave.fitness.R;
import com.wave.fitness.fragments.MapViewFragment;
import com.wave.fitness.fragments.OneFragment;
import com.wave.fitness.fragments.PedometerFragment;
import com.wave.fitness.fragments.ThreeFragment;
import com.wave.fitness.fragments.TwoFragment;
import com.wave.fitness.fragments.SpotifyFragmentActivity;

public class spotifyActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SpotifyFragmentActivity spot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(spot = new SpotifyFragmentActivity(), "Music");
        adapter.addFragment(new MapViewFragment(), "Running");
        adapter.addFragment(new PedometerFragment(), "Stats");
        viewPager.setAdapter(adapter);
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
}
