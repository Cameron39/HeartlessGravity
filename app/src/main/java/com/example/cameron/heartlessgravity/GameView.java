package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GameView extends Activity {

    TextView TV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameview);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MyActivity.theMessage);

        TV = findViewById(R.id.txtTest);
        TV.setText(message);
    }

}
