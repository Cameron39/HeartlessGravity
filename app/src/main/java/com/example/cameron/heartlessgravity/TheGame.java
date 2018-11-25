package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class TheGame extends Activity implements GestureDetector.OnGestureListener {

    String TAG = "TheGame";
    GameView gameView;
    Paint backPaint = new Paint();
    int shipXLoc = 100, shipYLoc = 100;
    double shipXVel = 5, shipYVel = 5, planetGravity = 1;
    Bitmap mainShip;
    private GestureDetector getGesture;
    Rect shipRect, touchRect;
    long startTime = 0, stopTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        this.setContentView(gameView);
        getGesture = new GestureDetector(this, this);
        startTime = SystemClock.elapsedRealtime();
        Log.d(TAG+"onCreate", "startTime: " +  startTime);
        mainShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1fall);

    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG+"onPause", "Inside");
        stopTime = SystemClock.elapsedRealtime();
        gameView.pause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG+"onResume", "Inside");
        //Keeps the right amount of time, else the velocity will instakill
        if (stopTime > 0) {
            long difTime = stopTime - startTime;
            startTime = SystemClock.elapsedRealtime() - difTime;
        }
        gameView.resume();
    }

    @Override
    protected void onDestroy(){
        finish();
        super.onDestroy();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (hasShipCollision(e1) || hasShipCollision(e2)) {
            Log.d(TAG + "OnFling-Pre", "Velocity: " + shipXVel + "," + shipYVel);
            shipXVel += (velocityX/1000);
            shipYVel += (velocityY/1000);
            Log.d(TAG + "OnFling-Post", "Velocity: " + shipXVel + "," + shipYVel);
            startTime = SystemClock.elapsedRealtime();
        }
        return true;
    }

    public boolean onTouchEvent (MotionEvent e) {

        switch (e.getActionMasked()) {
            case ACTION_UP:
                mainShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1fall);
                break;

            case ACTION_DOWN:
                if (hasShipCollision(e)) {
                    mainShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1acc);
                }
                break;
        }
        return getGesture.onTouchEvent(e);
    }

    protected boolean hasShipCollision(MotionEvent e){
        int cordX = (int)e.getX();
        int cordY = (int)e.getY();
        touchRect = new Rect(cordX, cordY, (cordX + 50), (cordY + 50));
        if (Rect.intersects(touchRect, shipRect)) {
            return true;
        } else {
            return false;
        }
    }

    public class GameView extends SurfaceView implements Runnable {

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
                shipRect = new Rect(shipXLoc, shipYLoc, shipXLoc + mainShip.getWidth(), shipYLoc + mainShip.getHeight());

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
                    Log.e(TAG+"GameSetup-Pause", e.getStackTrace().toString());
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
            canvas.drawColor(Color.GRAY);

            canvas.drawBitmap(mainShip, shipXLoc, shipYLoc, backPaint);

            //if doing collision, check here

            //Calculate the new velocity accounting for gravity
            float acceleration = ((SystemClock.elapsedRealtime() - startTime)/1000);
            shipYVel = shipYVel + (planetGravity * acceleration);
            Log.d(TAG + "GS-draw-Vel", "New Y Vel: " + shipYVel);

            shipXLoc += shipXVel;
            shipYLoc += shipYVel;

            //Hits a wall on the X axis, the sides
            if(shipXLoc < 0 || (shipXLoc + mainShip.getWidth()) > canvas.getWidth()) {
                shipXVel *= -1;
            }

            //Hits a wall on the Y axis, the top and bottom
            if(shipYLoc < 0 || (shipYLoc + mainShip.getHeight()) > canvas.getHeight()) {
                shipYVel *=-1;
                canvas.drawColor(Color.RED);
            }
        }
    }

}
