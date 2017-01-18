package edu.dartmouth.cs.xiankai_yang_myruns.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import edu.dartmouth.cs.xiankai_yang_myruns.R;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

    public void onClickSaveMap(View view) {
        finish();
    }

    public void onClickCancelMap(View view) {
        finish();
    }
}
