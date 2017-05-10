package com.wave.fitness;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.rogalabs.lib.model.SocialUser;

/**
 * Created by s6236422 on 10/05/2017.
 */

public class HamburgerMenu {
    
    public AccountHeader header;
    public Drawer menu;
    
    public HamburgerMenu(final AppCompatActivity activity, SocialUser user, Toolbar toolbar){

        
        header = new AccountHeaderBuilder()
                .withActivity(activity)
                //.withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getName()).withEmail(user.getEmail()).withIcon(activity.getResources().getDrawable(R.drawable.temp_profile)) // TODO: 10/05/2017 Replace with user.getIconURL using picasso
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();

        PrimaryDrawerItem dashboard = new PrimaryDrawerItem().withIdentifier(1).withName("Dashboard");
        SecondaryDrawerItem run = new SecondaryDrawerItem().withIdentifier(2).withName("Start A Run");
        SecondaryDrawerItem music = new SecondaryDrawerItem().withIdentifier(3).withName("Music");
        SecondaryDrawerItem past = new SecondaryDrawerItem().withIdentifier(4).withName("Previous Runs");
        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(5).withName("Settings");
        SecondaryDrawerItem logout = new SecondaryDrawerItem().withIdentifier(6).withName("Logout");

        menu = new DrawerBuilder()
                .withActivity(activity)
                .withAccountHeader(header)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        dashboard,run,music,past, new DividerDrawerItem(),settings,logout
                )
                .withOnDrawerItemClickListener(
                        new Drawer.OnDrawerItemClickListener(){
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                switch((int)drawerItem.getIdentifier()){
                                    case 1:
                                        activity.startActivity(new Intent(activity, DashboardActivity.class));
                                        break;
                                    case 2:
                                        activity.startActivity(new Intent(activity, RunActivity.class));
                                        break;
                                    case 3:
                                        activity.startActivity(new Intent(activity, MusicPlayerActivity.class));
                                        break;
                                    case 4:
                                        Log.e("Menu","4");
                                        break;
                                    case 5:
                                        activity.startActivity(new Intent(activity, Settings.class));
                                        break;
                                    case 6:
                                        Log.e("Menu","Logout");
                                        break;

                                }
                                return true;
                            }
                        }
                )
                .build();
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        menu.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }
}
