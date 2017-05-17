package com.wave.fitness;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
    }

    private void createHamburgerMenu(){
        menu = new HamburgerMenu(this, new Gson().fromJson(getSharedPreferences("com.wave.fitness", MODE_PRIVATE).getString("user", ""), SocialUser.class), toolbar);
    }

    private void populateRunData(){
        Data_RunStatistic stat;
        SimpleDateFormat parseFormat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        String dashboardDate = parseFormat.format(date);

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-9);
        title1.setText("Run on " + date);
        subtitle1.setText("Click to view");

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-8);
        title2.setText();
        subtitle2.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-7);
        title3.setText();
        subtitle3.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-6);
        title4.setText();
        subtitle4.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-5);
        title5.setText();
        subtitle5.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-4);
        title6.setText();
        subtitle6.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-3);
        title7.setText();
        subtitle7.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-2);
        title8.setText();
        subtitle8.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId-1);
        title9.setText();
        subtitle9.setText();

        stat = new Repo_RunStatistic(getApplicationContext()).getEntrybyID(lastRunId);
        title10.setText();
        subtitle10.setText();


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
                startPostRunActivity(lastRunId - 1;
            }
        });
        bar10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPostRunActivity(lastRunId);
            }
        });
    }

    private void startPostRunActivity(int runId){
        Intent postRun = new Intent(this, PostRunActivity.class);
        postRun.putExtra("runID", runId);
    }
}
