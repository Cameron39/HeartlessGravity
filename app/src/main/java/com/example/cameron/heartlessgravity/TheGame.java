package com.example.cameron.heartlessgravity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

//TODO: show score during flight (Gravity Level)
//TODO: Need a definative win or lose

public class TheGame extends Activity implements GestureDetector.OnGestureListener {

    String TAG = "TheGame-";
    GameView gameView;
    Paint backPaint = new Paint(), countPaint = new Paint(), redWarning = new Paint();
    //Paint redWarning = new Paint(); //TODO: cleanuip
    int shipXLoc = 400, shipYLoc = 100, playerLives = 3, tempTime = 0, gravityLevel = 0;
    final int gravityChange = 10; //how long it takes for the gravity to change
    double shipXVel = 0, shipYVel = 5, gravityPull = 0.5, maxVel = 10, minVel = -10;
    double flingXVel = 0, flingYVel = 0;
    Bitmap mainShip, accShip, fllShip;
    private GestureDetector getGesture;
    Rect shipRect, touchRect;
    long startTime = 0, stopTime = 0, flightTime = 0, totalFlightTime = 0;
    MediaPlayer mpBurst = new MediaPlayer();
    MediaPlayer mpFail = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        //gameView.setBackground(getResources().getDrawable(R.drawable.flightbackground));

        this.setContentView(gameView);
        getGesture = new GestureDetector(this, this);
        mpBurst = MediaPlayer.create(this, R.raw.rocketgo);
        mpFail = MediaPlayer.create(this, R.raw.failure);

        startTime = SystemClock.elapsedRealtime();
        totalFlightTime = startTime;
        flightTime = startTime;
        Log.d(TAG+"onCreate", "startTime: " +  startTime);
        accShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1acc);
        fllShip = BitmapFactory.decodeResource(getResources(), R.drawable.ship1fall);
        mainShip = fllShip;
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
            flingXVel = velocityX/500;
            flingYVel = velocityY/500;
            shipXVel += flingXVel;
            shipYVel += flingYVel;
            if (shipYVel > maxVel) { shipYVel = maxVel; }
            if (shipYVel < minVel) { shipYVel = minVel; }
            if (shipXVel > maxVel) { shipXVel = maxVel; }
            if (shipXVel < minVel) { shipXVel = minVel; }
            Log.d(TAG + "OnFling-Post", "Velocity: " + shipXVel + "," + shipYVel);
            startTime = SystemClock.elapsedRealtime();
        }
        return true;
    }

    public boolean onTouchEvent (MotionEvent e) {

        switch (e.getActionMasked()) {
            //todo: cleanup
            case ACTION_UP:
                mainShip = fllShip;
                break;

            case ACTION_DOWN:
                if (hasShipCollision(e)) {
                    mainShip = accShip;
                    mpBurst.start();
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
        }
        return false;
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
                //Need to specify the falling ship else the engine thrust can count as failure
                shipRect = new Rect(shipXLoc, shipYLoc, shipXLoc + fllShip.getWidth(), shipYLoc + fllShip.getHeight());

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
            tempTime = (int)((SystemClock.elapsedRealtime()-flightTime)/1000);
            Bitmap flightBackground = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.flightbackground), canvas.getWidth(), canvas.getHeight(),false);
            ColorFilter filter = new LightingColorFilter(Color.argb(10, 125, 0,0), 0);
            redWarning.setColorFilter(filter);
            redWarning.setTextSize(100);
            redWarning.setColor(Color.BLACK);
            backPaint.setAlpha(255);
            backPaint.setColor(Color.BLACK);
            backPaint.setTextSize(50);
            backPaint.setAntiAlias(true);
            backPaint.setStyle(Paint.Style.FILL);
            countPaint.setAlpha(150);
            countPaint.setColor(Color.BLACK);
            countPaint.setTextSize(500);
            countPaint.setAntiAlias(true);
            countPaint.setStyle(Paint.Style.FILL);

            canvas.drawBitmap(flightBackground, 0,0, backPaint);
            if (tempTime == gravityChange) {
                canvas.drawBitmap(flightBackground, 0, 0, redWarning);
                canvas.drawText("Gravity Increase", canvas.getWidth()/6, canvas.getHeight()/4, redWarning);
            } else if (tempTime == (gravityChange -1)) {
                canvas.drawText("1", (float)((canvas.getWidth())/2.8), (canvas.getHeight())/3, countPaint);
            } else if (tempTime == (gravityChange -2)) {
                canvas.drawText("2", (float)((canvas.getWidth())/2.8), (canvas.getHeight())/3, countPaint);
            } else if (tempTime == (gravityChange -3)) {
                canvas.drawText("3", (float)((canvas.getWidth())/2.8), (canvas.getHeight())/3, countPaint);
            } else {
                canvas.drawBitmap(flightBackground, 0,0, backPaint);
            }

            canvas.drawBitmap(mainShip, shipXLoc, shipYLoc, backPaint);

            //For debugging purposes
            //canvas.drawText("RY:" + String.valueOf(shipYVel), 50,50, backPaint);
            //canvas.drawText("RX:" + String.valueOf(shipXVel), 50, 80, backPaint);
            //canvas.drawText("FY:" + String.valueOf(flingYVel), 500, 50, backPaint);
            //canvas.drawText("FX:" + String.valueOf(flingXVel), 500, 80, backPaint);

            canvas.drawText("Gravity Level: " + gravityLevel, 50, 50, backPaint);
            //canvas.drawText("Gravity Increase In: " + (gravityChange-((SystemClock.elapsedRealtime() - flightTime)/1000)), 50, 50, backPaint);
            canvas.drawText("Lives:" + playerLives, 50, (canvas.getHeight()-50), backPaint);

            //Calculate the new velocity accounting for gravity
            float acceleration = ((SystemClock.elapsedRealtime() - startTime)/1000);
            shipYVel = shipYVel + (gravityPull * acceleration);
            if (shipYVel > maxVel) {shipYVel = maxVel;}
            if (shipYVel < minVel) {shipYVel = minVel;}

            Log.d(TAG + "GS-draw-Vel", "New Y Velocity: " + shipYVel);

            shipXLoc += shipXVel;
            shipYLoc += shipYVel;

            //Hits a wall on the X axis, the sides
            if(shipXLoc < 0 || (shipXLoc + fllShip.getWidth()) > canvas.getWidth()) {
                shipXVel *= -1;
                shipYVel = maxVel;
            }

            //Hits a wall on the Y axis, the top and bottom
            if(shipYLoc < 0 || (shipYLoc + fllShip.getHeight()) > canvas.getHeight()) {
                shipYVel *=-1;
                canvas.drawColor(Color.RED);
                failure();
            }

            //Check if time to add more to the gravity, increase if it is. Reset the flight time
            if (tempTime > gravityChange) {
                gravityPull += 0.5;
                maxVel += 5;
                minVel -= 5;
                playerLives++;
                gravityLevel++;
                flightTime = SystemClock.elapsedRealtime();
            }
        }

        public void failure(){
            playerLives -=1; //decrement a life
            mpFail.start();
            Log.d(TAG + "GS-failure", "Player Lives: " + playerLives);
            if (playerLives > 0) {
                Log.d(TAG + "GS-failure", "Player Still alive with " + playerLives);
            } else {
                totalFlightTime = (int)((SystemClock.elapsedRealtime() - totalFlightTime)/1000);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("gravity", String.valueOf(gravityLevel));
                returnIntent.putExtra("flightTime", String.valueOf(totalFlightTime));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }
}
