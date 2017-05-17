package com.wave.fitness;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rogalabs.lib.model.SocialUser;

import java.text.DateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PreviousRunActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar) Toolbar toolbar;

    @InjectView(R.id.bar1) LinearLayout bar1;
    @InjectView(R.id.titleone) TextView title1;
    @InjectView(R.id.subtitleone) TextView subtitle1;

    @InjectView(R.id.bar2) LinearLayout bar2;
    @InjectView(R.id.titletwo) TextView title2;
    @InjectView(R.id.subtitletwo) TextView subtitle2;

    @InjectView(R.id.bar3) LinearLayout bar3;
    @InjectView(R.id.titlethree) TextView title3;
    @InjectView(R.id.subtitlethree) TextView subtitle3;

    @InjectView(R.id.bar4) LinearLayout bar4;
    @InjectView(R.id.titlefour) TextView title4;
    @InjectView(R.id.subtitlefour) TextView subtitle4;

    @InjectView(R.id.bar5) LinearLayout bar5;
    @InjectView(R.id.titlefive) TextView title5;
    @InjectView(R.id.subtitlefive) TextView subtitle5;

    @InjectView(R.id.bar6) LinearLayout bar6;
    @InjectView(R.id.titlesix) TextView title6;
    @InjectView(R.id.subtitlesix) TextView subtitle6;

    @InjectView(R.id.bar7) LinearLayout bar7;
    @InjectView(R.id.titleseven) TextView title7;
    @InjectView(R.id.subtitleseven) TextView subtitle7;

    @InjectView(R.id.bar8) LinearLayout bar8;
    @InjectView(R.id.titleeight) TextView title8;
    @InjectView(R.id.subtitleeither) TextView subtitle8;

    @InjectView(R.id.bar9) LinearLayout bar9;
    @InjectView(R.id.titlenine) TextView title9;
    @InjectView(R.id.subtitlenine) TextView subtitle9;

    @InjectView(R.id.bar10) LinearLayout bar10;
    @InjectView(R.id.titleten) TextView title10;
    @InjectView(R.id.subtitleten) TextView subtitle10;

    private HamburgerMenu menu;
    private int lastRunId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previousruns);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lastRunId = getSharedPreferences("com.wave.fitness", MODE_PRIVATE).getInt("runID", 0);

        ButterKnife.inject(this);
        createHamburgerMenu();
        populateRunData();
        injectOnClickListener();
    }

    private void createHamburgerMenu(){
        menu = new HamburgerMenu(this, new Gson().fromJson(getSharedPreferences("com.wave.fitness", MODE_PRIVATE).getString("user", ""), SocialUser.class), toolbar);
    }

    private void populateRunData(){
        Data_RunStatistic stat;
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-9);
        date.setTime(stat.date);

        title1.setText("Run on " + parseFormat.format(date));
        subtitle1.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-8);
        date.setTime(stat.date);
        title2.setText("Run on " + parseFormat.format(date));
        subtitle2.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-7);
        date.setTime(stat.date);
        title3.setText("Run on " + parseFormat.format(date));
        subtitle3.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-6);
        date.setTime(stat.date);
        title4.setText("Run on " + parseFormat.format(date));
        subtitle4.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-5);
        date.setTime(stat.date);
        title5.setText("Run on " + parseFormat.format(date));
        subtitle5.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-4);
        date.setTime(stat.date);
        title6.setText("Run on " + parseFormat.format(date));
        subtitle6.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-3);
        date.setTime(stat.date);
        title7.setText("Run on " + parseFormat.format(date));
        subtitle7.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-2);
        date.setTime(stat.date);
        title8.setText("Run on " + parseFormat.format(date));
        subtitle8.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-1);
        date.setTime(stat.date);
        title9.setText("Run on " + parseFormat.format(date));
        subtitle9.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId);
        date.setTime(stat.date);
        title10.setText("Run on " + parseFormat.format(date));
        subtitle10.setText("Click to view");
    }

    private void injectOnClickListener(){

        bar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 9);
            }
        });
        bar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 8);
            }
        });
        bar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 7);
            }
        });
        bar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 6);
            }
        });
        bar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 5);
            }
        });

        bar6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 4);
            }
        });
        bar7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 3);
            }
        });
        bar8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 2);
            }
        });
        bar9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId - 1);
            }
        });

        bar10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId);
                Log.e("prev", "click 10");
            }
        });
    }

    private void startPostRunActivity(int runId){
        Intent postRun = new Intent(this, PostRunActivity.class);
        postRun.putExtra("runID", runId);
        this.startActivity(postRun);
    }
}
