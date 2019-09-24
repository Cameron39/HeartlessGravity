/*
Cameron Pitcel
CSC 470 Intro to Android Fall 2018
Project 5, Game

Idea is a ship that is being pulled down towards a planet's surface that must be flung up
using the player's finger.
Gravity increases every 10 seconds along with the min/max velocity
Player starts out with 3 lives and increases by one with every gravity increase
Touching the top or the bottom will reduce a life.
When the players run out of lives, the game is done.

The name, Heartless Gravity, is a play on words from the idea that Gravity is a Heartless B*tch
All credit for images and icon used goes to Freepik, I take no credit for them.
 */
package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MyActivity extends Activity {

    public static final String restartStatus = "Status";
    final private boolean restart = false;
    private String flightTime = "0", gravityLevel = "0";
    TextView txtReturnTime, txtReturnGravity, txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtReturnTime = findViewById(R.id.txtReturnTime);
        txtReturnGravity = findViewById(R.id.txtReturnGravity);
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
                gravityLevel =  data.getStringExtra("gravity");
                flightTime = "Flight Time: " + data.getStringExtra("flightTime") +"s";
                //TODO: Has the Gravity Level part!  Need to re-arrange!
                switch (Integer.valueOf(gravityLevel)) {
                    case 0:
                        txtResult.setText(getString(R.string.lose));
                        break;
                    case 1:
                        txtResult.setText(getString(R.string.winbarely));
                        break;
                    default:
                        txtResult.setText(getString(R.string.win));
                }
                gravityLevel = "Gravity Level: " + gravityLevel;
                txtReturnTime.setText(flightTime);
                txtReturnGravity.setText(gravityLevel);
            }
        }
    }
}
