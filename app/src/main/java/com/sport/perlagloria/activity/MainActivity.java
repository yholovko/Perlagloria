package com.sport.perlagloria.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sport.perlagloria.R;
import com.sport.perlagloria.util.SharedPreferenceKey;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        SharedPreferences sPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        final int savedTeamid = sPref.getInt(SharedPreferenceKey.TEAM_ID, -1);
        String name = sPref.getString(SharedPreferenceKey.TEAM_NAME, null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if (savedTeamid == -1) {
                    intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);
                } else {
                    intent = new Intent(getApplicationContext(), TeamActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 1000);   //show splashscreen during 1 sec

    }
}
