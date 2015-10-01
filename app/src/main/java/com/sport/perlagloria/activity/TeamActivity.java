package com.sport.perlagloria.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.sport.perlagloria.R;
import com.sport.perlagloria.util.SharedPreferenceKey;

public class TeamActivity extends AppCompatActivity {
    //private TabLayout mainTabLayout;
    private TextView firstTab;
    private TextView secondTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        firstTab = (TextView) findViewById(R.id.firstTab);
        firstTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                MyTeamFragment targetFragment = new MyTeamFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.tab_fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }
        });

        //mainTabLayout = (TabLayout) findViewById(R.id.main_tab_Layout);

        SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        getSupportActionBar().setTitle(sPref.getString(SharedPreferenceKey.TEAM_NAME, "Null"));


        //pagerAdapter.
    }

}