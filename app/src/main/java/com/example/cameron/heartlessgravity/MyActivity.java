package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
//TODO: Make an Icon!

public class MyActivity extends Activity {

    public static final String restartStatus = "Status";
    private boolean restart = false;
    private String flightTime = "0", gravityLevel = "0";
    TextView txtReturnTime, txtReturnGravity, txtTips, txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtReturnTime = findViewById(R.id.txtReturnTime);
        txtReturnGravity = findViewById(R.id.txtReturnGravity);
        txtTips = findViewById(R.id.txtTips);
        txtResult = findViewById(R.id.txtResult);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, TheGame.class);
        intent.putExtra(restartStatus, restart);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) { //from TheGame.java
            if (resultCode == Activity.RESULT_OK){
                gravityLevel = "Gravity Level: " + data.getStringExtra("gravity");
                flightTime = "Flight Time: " + data.getStringExtra("flightTime") +"s";
                txtReturnTime.setText(flightTime);
                txtReturnGravity.setText(gravityLevel);
                switch (Integer.valueOf(gravityLevel)) {
                    case 0:
                        txtResult.setText(getString(R.string.lose));
                        break;
                    case 1:
                        txtResult.setText(getString(R.string.winbarely));
                        break;
                    default:
                        txtResult.setText(R.string.win);
                }
            }
        }
    }
}
