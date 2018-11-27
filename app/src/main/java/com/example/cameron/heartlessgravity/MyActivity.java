package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyActivity extends Activity {

    public static final String restartStatus = "Status";
    private boolean restart = false;
    private String flightTime = "0", gravityLevel = "0";
    private StringBuilder output = new StringBuilder(); //must initialize else error!
    TextView txtReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtReturn = findViewById(R.id.txtReturn);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, TheGame.class);
        intent.putExtra(restartStatus, restart); //maybe pass if it is a pause or restart
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) { //from TheGame.java
            if (resultCode == Activity.RESULT_OK){
                gravityLevel = data.getStringExtra("gravity");
                flightTime = data.getStringExtra("flightTime");
                output.append("Gravity: ").append(gravityLevel).append(" Time:").append(flightTime);
                txtReturn.setText(output.toString());
            }

        }

    }
}
