package com.sport.perlagloria.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sport.perlagloria.R;
import com.sport.perlagloria.model.Customer;
import com.sport.perlagloria.model.Division;
import com.sport.perlagloria.model.Tournament;

public class ChooseTeamActivity extends AppCompatActivity implements SelectChampionshipFragment.OnChampionshipPassListener, SelectTournamentFragment.OnTournamentPassListener,
        SelectDivisionFragment.OnDivisionPassListener {
    public static final int SELECT_CHAMPIONSHIP = 1;
    public static final int SELECT_TOURNAMENT = 2;
    public static final int SELECT_DIVISION = 3;
    public static final int SELECT_TEAM = 4;

    private int currentState = SELECT_CHAMPIONSHIP;
    private ImageView triangleImageView;
    private RelativeLayout bottomLayout; //layout with "next" triangle button

    private Customer selectedChampionship;
    private Tournament selectedTournament;
    private Division selectedDivision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        bottomLayout = (RelativeLayout) findViewById(R.id.bottomLayout);
        bottomLayout.setOnClickListener(new NextClickListener());
        triangleImageView = (ImageView) findViewById(R.id.triangleImageView);
        triangleImageView.setOnClickListener(new NextClickListener());

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
                                //.addToBackStack(null)
                        .commit();
                break;
            case SELECT_TOURNAMENT:
                targetFragment = SelectTournamentFragment.newInstance(selectedChampionship.getId(), selectedChampionship.getName());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
            case SELECT_DIVISION:
                targetFragment = SelectDivisionFragment.newInstance(selectedChampionship.getName(), selectedTournament.getId(), selectedTournament.getName());
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
                break;
            case SELECT_TEAM:
                break;
        }
    }

    /**
     * Check if item was selected from checkbox list
     *
     * @return true if was selected, false otherwise
     */
    private boolean checkSelection() {
        if (currentState == SELECT_CHAMPIONSHIP && selectedChampionship == null)
            return false;
        if (currentState == SELECT_TOURNAMENT && selectedTournament == null)
            return false;
        if (currentState == SELECT_DIVISION && selectedDivision == null)
            return false;

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentState > SELECT_CHAMPIONSHIP) {
            currentState--;
        }
    }

    @Override
    public void onChampionshipPass(Customer customer) {
        selectedChampionship = customer;
    }

    @Override
    public void onTournamentPass(Tournament tournament) {
        selectedTournament = tournament;
    }

    @Override
    public void onDivisionPass(Division division) {
        selectedDivision = division;
    }

    private class NextClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (currentState < SELECT_TEAM && checkSelection()) {
                currentState++;
                loadFragment();
                triangleImageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_triangle));
            }
        }
    }
}
