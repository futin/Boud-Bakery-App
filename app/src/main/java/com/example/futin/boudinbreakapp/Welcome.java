package com.example.futin.boudinbreakapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


public class Welcome extends Activity {

    private final int DELAYED_TIME = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent boudin = new Intent(Welcome.this, MainActivity.class);
                startActivity(boudin);
                finish();
            }
        }, DELAYED_TIME);
    }
}
