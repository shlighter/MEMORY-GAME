package com.example.memory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

/**
 *High Scores Activity. This Activity displays the high scores list using ListAdapter
 * @author Shai
 */

public class HighScoreActivity extends AppCompatActivity {


    ArrayList<UserScore> scoresList;
    GetHighScores gh;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        scoresList= new ArrayList<>();
        ListView lv =  (ListView) findViewById(R.id.list);
        adapter= new ListAdapter(this, R.layout.row2, scoresList);
        lv.setAdapter(adapter);
        gh= new GetHighScores(HighScoreActivity.this);
    }


    @Override
    protected void onStop() {
        super.onStop();
       finish();
    }
}
