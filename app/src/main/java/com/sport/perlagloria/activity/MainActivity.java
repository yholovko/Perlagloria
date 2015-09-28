package com.sport.perlagloria.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.sport.perlagloria.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {  //show splashscreen during 1 sec
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), ChooseTeamActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
