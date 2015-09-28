package com.sport.perlagloria.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sport.perlagloria.R;

public class ChooseTeamActivity extends AppCompatActivity {
    public static final int SELECT_CHAMPIONSHIP = 1;
    public static final int SELECT_TOURNAMENT = 2;
    public static final int SELECT_DIVISION = 3;
    public static final int SELECT_TEAM = 4;

    private int currentState = SELECT_CHAMPIONSHIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        loadFragment();
    }

    private void loadFragment() {
        Fragment targetFragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (currentState) {
            case SELECT_CHAMPIONSHIP:
                targetFragment = new SelectChampionshipFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case SELECT_TOURNAMENT:
                break;
            case SELECT_DIVISION:
                break;
            case SELECT_TEAM:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
