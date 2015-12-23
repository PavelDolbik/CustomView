package com.example.pdolbik.customseekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by p.dolbik on 23.12.2015.
 */
public class MyView extends View {

    private int radius      = 300;
    private int maxProgress = 100;
    private int maxAngel    = 360;


    private int canvasCenterX;
    private int canvasCenterY;

    private Paint    ovalPaint;
    private Paint    progressPaint;
    private RectF    rectF;

    private Drawable mThumb;
    private int      thumbHalfHeight;
    private int      thumbHalfWidth;
    private int      thumbAngle = 90;

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
    }

    private void initMyView() {
        ovalPaint = new Paint();
        ovalPaint.setStrokeWidth(3);
        ovalPaint.setColor(Color.BLUE);
        ovalPaint.setAntiAlias(true);
        ovalPaint.setStyle(Paint.Style.STROKE);

        progressPaint = new Paint();
        progressPaint.setStrokeWidth(8);
        progressPaint.setColor(Color.RED);
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);

        mThumb = ContextCompat.getDrawable(getContext(), R.drawable.arc_trumb);
        thumbHalfHeight =  mThumb.getIntrinsicHeight() / 2;
        thumbHalfWidth  =  mThumb.getIntrinsicWidth() / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);

        canvasCenterX = parentWidth/2;
        canvasCenterY = parentHeight/2;

        rectF = new RectF(canvasCenterX- radius, canvasCenterY - radius, canvasCenterX + radius, canvasCenterY + radius);
        setThumbPosition(0);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawOval(rectF, ovalPaint);
        canvas.drawArc(rectF, 90, thumbAngle, false, progressPaint);
        mThumb.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                changeProgress(event.getX(), event.getY());
                break;
        }

        return true;
    }

    private void setThumbPosition( int progress) {
        //Устанавливаем thumb в центр круга
        int thumbLeft   = (canvasCenterX - thumbHalfWidth);
        int thumbRight  = (canvasCenterX + thumbHalfWidth );
        int thumbTop    = (canvasCenterY - thumbHalfHeight);
        int thumbBottom = (canvasCenterY + thumbHalfHeight);

        //Смещаем thumb в зависимости от прогреса
        thumbAngle = (int) (3.6 * progress);
        int thumbX  = (int) (radius * Math.cos(Math.toRadians(thumbAngle + 90)));
        int thumbY  = (int) (radius * Math.sin(Math.toRadians(thumbAngle + 90)));

        mThumb.setBounds(thumbLeft+thumbX, thumbTop+thumbY, thumbRight+thumbX, thumbBottom+thumbY);
    }

    private void changeProgress(float x, float y) {
        double mTouchAngle = getTouchDegrees(x, y);
        int progress = getProgressForAngle(mTouchAngle);
        setThumbPosition(progress);
        invalidate();
    }

    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - canvasCenterX;
        float y = yPos - canvasCenterY;

        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2) - Math.toRadians(180));

        if (angle < 0)
            angle = 360 + angle;

        return angle;
    }

    private int getProgressForAngle(double angle) {
        int touchProgress = (int) Math.round(valuePerDegree() * angle);
        return touchProgress;
    }

    private float valuePerDegree() {
        return (float) maxProgress / maxAngel;
    }
}
