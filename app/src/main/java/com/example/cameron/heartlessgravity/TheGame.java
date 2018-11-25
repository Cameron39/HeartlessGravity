package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TheGame extends Activity {

    //TextView TV;
    GameView gameView;
    Paint backPaint = new Paint();
    int shipXLoc = 100, shipYLoc = 100, shipXSpeed = 10, shipYSpeed = 10;
    Bitmap mainShip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.gameview);
        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MyActivity.theMessage);
        //TV = findViewById(R.id.txtTest);
        //TV.setText(message);

        gameView = new GameView(this);
        this.setContentView(gameView);

        //Todo: change this to the ship graphic
        mainShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1fall);

    }

    @Override
    protected void onPause(){
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        gameView.resume();
    }

    public class GameView extends SurfaceView implements Runnable {

        String Tag = "GameSetup";
        Thread ViewThread = null;
        SurfaceHolder holder;
        boolean threadInFocus = true;

        public GameView(Context context) {
            super(context);
            holder = this.getHolder();
        }

        @Override
        public void run() {

            while(threadInFocus) {
                if (!holder.getSurface().isValid()){
                    continue;
                }

                Canvas gameCanvas = holder.lockCanvas();
                //setup collision graphics here

                drawTheCanvas(gameCanvas);
                holder.unlockCanvasAndPost(gameCanvas);
            }

        }

        public void pause(){
            threadInFocus = false;
            while(true){
                try {
                    ViewThread.join();
                } catch (InterruptedException e) {
                    Log.e(Tag, e.getStackTrace().toString());
                }
                break;
            }
            ViewThread = null;
        }

        public void resume(){
            threadInFocus = true;
            ViewThread = new Thread (this);
            ViewThread.start();
        }

        protected void drawTheCanvas(Canvas canvas) {
            backPaint.setAlpha(255);
            canvas.drawColor(Color.RED);

            canvas.drawBitmap(mainShip, shipXLoc, shipYLoc, backPaint);

            //if doing collision, check here

            shipXLoc += shipXSpeed;
            shipYLoc += shipYSpeed;

            //Hits a wall on the X axis, the sides
            if(shipXLoc < 0 || (shipXLoc + mainShip.getWidth()) > canvas.getWidth()) {
                shipXSpeed *= -1;
            }

            //Hits a wall on the Y axis, the top and bottom
            if(shipYLoc < 0 || (shipYLoc + mainShip.getHeight()) > canvas.getHeight()) {
                shipYSpeed *=-1;
            }
        }
    }

}
