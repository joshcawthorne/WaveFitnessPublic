package com.wave.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
import com.spotify.sdk.android.player.SpotifyPlayer;

public class DashboardActivity extends AppCompatActivity {

    Drawer menu;

    private SpotifyPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                //.withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Josh Cawthorne").withEmail("joshcawthorne97@gmail.com")/*.withIcon(getResources().getDrawable(R.drawable.profile))*/
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem music = new PrimaryDrawerItem().withIdentifier(1).withName("Music");

         menu = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        music, new SecondaryDrawerItem().withName("Start A Run"), new SecondaryDrawerItem().withName("Past Runs"),
                        new DividerDrawerItem(), new SecondaryDrawerItem().withName("Settings"), new SecondaryDrawerItem().withName("Logout")
                )
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener(){
                             @Override
                             public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                 // do something with the clicked item :D
                                 startActivity(new Intent(DashboardActivity.this, DemoActivity.class));
                                return true;
                             }
                        }
                )
                .build();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        menu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);

    }

    public void onRunButtonClicked(View view) {
        startActivity(new Intent(DashboardActivity.this, RunActivity.class));
    }

    private boolean isLoggedIn() {
        return mPlayer != null && mPlayer.isLoggedIn();
    }

    @Override
    public void onBackPressed() {
        if(menu.isDrawerOpen()){
            menu.closeDrawer();
        }
        else{
            super.onBackPressed();
        }

    }

}
