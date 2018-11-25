package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.InputStream;

public class GameView extends Activity {

    TextView TV;
    GameSetup gameSetup;
    Paint backPaint = new Paint();
    int shipXLoc = 100, shipYLoc = 100, shipXSpeed = 100, shipYSpeed = 100;
    Bitmap mainShip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.gameview);
        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MyActivity.theMessage);
        //TV = findViewById(R.id.txtTest);
        //TV.setText(message);

        gameSetup = new GameSetup(this);
        this.setContentView(gameSetup);
        InputStream inputStream;
        //Todo: change this to the ship graphic
        mainShip = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);


    }

    @Override
    protected void onPause(){
        super.onPause();
        gameSetup.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameSetup.resume();
    }

    public class GameSetup extends SurfaceView implements Runnable {

        String Tag = "GameSetup";
        Thread SurfaceThread = null;
        SurfaceHolder surfaceHolder;
        boolean threadInFocus = true;

        public GameSetup(Context context) {
            super(context);
            surfaceHolder = this.getHolder();
        }

        @Override
        public void run() {

            while(threadInFocus) {
                if (!surfaceHolder.getSurface().isValid()){
                    continue;
                }

                Canvas gameCanvas = surfaceHolder.lockCanvas();
                //setup collision graphics here

                drawTheCanvas(gameCanvas);
                surfaceHolder.unlockCanvasAndPost(gameCanvas);
            }

        }

        public void pause(){
            threadInFocus = false;
            while(true){
                try {
                    SurfaceThread.join();
                } catch (InterruptedException e) {
                    Log.e(Tag, e.getStackTrace().toString());
                }
                break;
            }
            SurfaceThread = null;
        }

        public void resume(){
            threadInFocus = true;
            SurfaceThread = new Thread (this);
            SurfaceThread.start();
        }

        protected void drawTheCanvas(Canvas canvas) {
            backPaint.setAlpha(255);
            canvas.drawColor(Color.RED);

            canvas.drawBitmap(mainShip, shipXLoc, shipYLoc, backPaint);

            //if doing collision, check here

            shipXLoc += shipXSpeed;
            shipYLoc += shipYSpeed;

            //Hits a wall on the X axis, the sides
            if(shipXLoc < 0 || shipXLoc > canvas.getWidth()) {
                shipXSpeed *= -1;
            }

            //Hits a wall on the Y axis, the top and bottom
            if(shipYLoc < 0 || shipYLoc > canvas.getHeight()) {
                shipYSpeed *=-1;
            }
        }
    }

}
