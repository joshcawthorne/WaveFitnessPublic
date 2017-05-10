package com.wave.fitness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

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

import java.util.Date;
import java.util.Random;

import studios.codelight.smartloginlibrary.UserSessionManager;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DashboardActivity extends AppCompatActivity implements Animation.AnimationListener {

    Drawer menu;
    Toolbar toolbar;
    SharedPreferences prefs = null;

    Animation animFadein;

    SpotifyCore core;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        core = ((SpotifyCore)getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefs = getSharedPreferences("com.wave.fitness", MODE_PRIVATE);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.standard);

        animFadein.setAnimationListener(this);

        if(prefs.getBoolean("firstrun", true)){
            startActivity(new Intent(DashboardActivity.this, setupActivity.class));
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Montserrat-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        SimpleDateFormat parseFormat = new SimpleDateFormat("EEEE");
        Date date =new Date();
        String dashboardDate = parseFormat.format(date);

        Random prefix = new Random();
        String[] prefixs = new String[] { "Happy ", "It's ",
                "Don't you just love ", "Let's get you through ", "Welcome to "};

        String prefixString = "Happy ";

        int INDEXn = prefix.nextInt(prefixs.length);
        for (int i2 = 0; i2 < INDEXn; i2++) {
            prefixString = (String) (prefixs[INDEXn]);
        }

        Random endSent = new Random();
        String[] endSentence = new String[] {"!", "!", "?", ".", "."};

        String endSentString = "!";

        for (int i2 = 0; i2 < INDEXn; i2++) {
            endSentString = (String) (endSentence[INDEXn]);
        }

        TextView curDate = (TextView)findViewById(R.id.dashDate);
        curDate.setText(prefixString + dashboardDate + ", Josh" + endSentString);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (prefs.getBoolean("firstrun", true)) {
            Intent firstTime = new Intent(DashboardActivity.this, setupActivity.class);
            DashboardActivity.this.startActivityForResult(firstTime, 22);
        }
        else{
            new HamburgerMenu(this, UserSessionManager.getCurrentUser(this.getApplicationContext()), toolbar);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        new HamburgerMenu(this, UserSessionManager.getCurrentUser(this.getApplicationContext()), toolbar);
    }

    public void onRunButtonClicked(View view) {
        startActivity(new Intent(DashboardActivity.this, RunActivity.class));
    }

    @Override
    public void onBackPressed() {
        if(menu.isDrawerOpen()){
            menu.closeDrawer();
        }
        else{
            super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }

    }

    public void startTemp(View view) {
        startActivity(new Intent(DashboardActivity.this, spotifyActivity.class));
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for fade in animation
        if (animation == animFadein) {
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

}
