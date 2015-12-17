package com.example.pdolbik.moving;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by p.dolbik on 17.12.2015.
 */
public class MyCustomView extends View {

    private int canvasWidth;
    private int canvasHeight;

    private Bitmap androidBitmap;
    private Matrix androidMatrix;
    private int    centerX;
    private int    centerY;

    private GestureDetector detector;

    private Matrix       animateMatrix;
    private Interpolator interpolator;
    private long         startTime;
    private long         endTime;
    private float        totalAnimDx;
    private float        totalAnimDy;


    public MyCustomView(Context context) {
        super(context);
        Log.d("Pasha", "constructor");
        androidMatrix = new Matrix();
        androidBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android);
        detector = new GestureDetector(context, new MyGestureListener());
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("Pasha", "onSizeChanged");
        canvasWidth = MeasureSpec.getSize(w);
        canvasHeight = MeasureSpec.getSize(h);
        centerX = (canvasWidth - androidBitmap.getWidth()) / 2;
        centerY = (canvasHeight - androidBitmap.getHeight()) / 2;
        androidMatrix.postTranslate(centerX, centerY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("Pasha", "onDraw");
        Log.d("Pasha", "" + androidMatrix.toShortString());
        canvas.drawBitmap(androidBitmap, androidMatrix, null);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            onResetLocation();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
            onMove(-distanceX, -distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
            float distanceTimeFactor = 0.2f;
            float totalDx = (distanceTimeFactor * velocityX / 2);
            float totalDy = (distanceTimeFactor * velocityY / 2);
            onAnimateMove(totalDx, totalDy, (long) (1000 * distanceTimeFactor));
            return true;
        }
    }

    public void onMove(float dx, float dy) {
        androidMatrix.postTranslate(dx, dy);
        invalidate();
    }

    public void onResetLocation() {
        androidMatrix.reset();
        androidMatrix.postTranslate(centerX, centerY);
        invalidate();
    }

    public void onAnimateMove(float dx, float dy, long duration) {
        animateMatrix = new Matrix(androidMatrix);
        interpolator  = new OvershootInterpolator();
        startTime = System.currentTimeMillis();
        endTime   = startTime + duration;
        totalAnimDx = dx;
        totalAnimDy = dy;
        post(new Runnable() {
            @Override
            public void run() {
                onAnimateStep();
            }
        });
    }

    private void onAnimateStep() {
        long curTime = System.currentTimeMillis();
        float percentTime = (float) (curTime - startTime) / (float) (endTime - startTime);
        float percentDistance = interpolator.getInterpolation(percentTime);
        float curDx = percentDistance * totalAnimDx;
        float curDy = percentDistance * totalAnimDy;
        androidMatrix.set(animateMatrix);
        onMove(curDx, curDy);

        if (percentTime < 1.0f) {
            post(new Runnable() {
                @Override
                public void run() {
                    onAnimateStep();
                }
            });
        }
    }
}
