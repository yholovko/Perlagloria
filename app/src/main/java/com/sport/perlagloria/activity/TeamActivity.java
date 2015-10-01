package com.sport.perlagloria.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

    private TextView toolbarTitle;
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        toolbarTitle = (TextView) mainToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("");

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
        setToolbarTitle(sPref.getString(SharedPreferenceKey.TEAM_NAME, "Null"));


        //pagerAdapter.
    }

    public void setToolbarTitle(String text) {
        toolbarTitle.setText(text);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showExitAlertDialog();
    }

    private void showExitAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(getString(R.string.sure_exit_dialog));
        builder.setNegativeButton(getString(R.string.sure_exit_dialog_no), null);
        builder.setPositiveButton(getString(R.string.sure_exit_dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                System.exit(0);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
