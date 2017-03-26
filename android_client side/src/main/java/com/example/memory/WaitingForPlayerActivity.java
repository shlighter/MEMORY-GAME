package com.example.memory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
* Created by Shai on 2/17/2017.
 * This Activity shows a waiting clock image and waits to get a second player to play with by a Waiting object.
*/

public class WaitingForPlayerActivity extends AppCompatActivity {

    Waiting waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_player);
        Intent intent= getIntent();
        waiting= new Waiting(intent.getStringExtra("username"),WaitingForPlayerActivity.this);
    }
}
