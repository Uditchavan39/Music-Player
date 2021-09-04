package com.thursday.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

public class Splashscreen extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
    int secondsdelyed=1;
    new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(Splashscreen.this,MainActivity.class));
            finish();
        }
    },secondsdelyed*1000);

    }
}
