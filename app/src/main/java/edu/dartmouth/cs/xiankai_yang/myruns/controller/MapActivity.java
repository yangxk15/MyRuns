package edu.dartmouth.cs.xiankai_yang.myruns.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.dartmouth.cs.xiankai_yang.myruns.R;

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
