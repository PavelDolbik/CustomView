package com.example.pdolbik.swipeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by p.dolbik on 30.12.2015.
 */
public class MyView extends View {

    private Bitmap bitmap;
    private Paint  paint;
    private GestureDetector detector;

    private int   square;
    private int   widthSquare;
    private float touchX;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.line);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        widthSquare = bitmap.getWidth()/5;

        detector = new GestureDetector(getContext(), new MyGestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.setMeasuredDimension(bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        canvas.drawBitmap(bitmap,  (square*widthSquare)- widthSquare*4, 0, null);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP && square > 0) {
            MyAnimation animation = new MyAnimation(square);
            animation.setDuration(200);
            startAnimation(animation);
        }
        return true;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            touchX = e.getX();
            if(touchX < widthSquare) {
                square = 0;
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            updateShowSquare(e2.getX());
            return true;
        }
    }

    private void updateShowSquare(float position) {
        square = (int) (position/widthSquare);
        if(square > 4) {
            square = 4;
        }
        invalidate();
    }

    private class MyAnimation extends Animation {
        private int showSquare;

        public MyAnimation(int square) {
            this.showSquare = square;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float position = showSquare * widthSquare;
            float result = position - (position * interpolatedTime);
            updateShowSquare(result);
        }
    }
}
