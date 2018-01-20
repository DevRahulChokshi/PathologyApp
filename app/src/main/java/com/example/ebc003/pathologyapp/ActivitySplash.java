package com.example.ebc003.pathologyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ActivitySplash extends AppCompatActivity {
TextView tv1,t2,t3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        tv1= findViewById(R.id.t1);
        t2= findViewById(R.id.t2);
        t3= findViewById(R.id.textView9);
        Typeface face= Typeface.createFromAsset(getAssets(), "font/Vidaloka-Regular.ttf");
        tv1.setTypeface(face);
        t2.setTypeface(face);
        t3.setTypeface(face);
        final String PREF_NAME = "AndroidHivePref";

        SharedPreferences sharedpreferences;
        int SPLASH_TIME_OUT = 3000;
        sharedpreferences = getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        String value = sharedpreferences.getString("test", "test");

        if (value.equalsIgnoreCase("")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // ActivityDashboard your app main activity
                    Intent intent = new Intent(ActivitySplash.this,
                            ActivityLogin.class);


                    startActivity(intent);

                }
            }, SPLASH_TIME_OUT);
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(ActivitySplash.this,
                            ActivityLogin.class);


                    startActivity(intent);
                }
            }, SPLASH_TIME_OUT);


        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}