package com.example.pdolbik.ringanimation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by p.dolbik on 28.12.2015.
 */
public class Ring extends View {

    private static final int START_ANGLE_POINT = 90;

    private int canvasCenterX;
    private int canvasCenterY;

    private Paint paint;
    private RectF rect;

    private float angle;

    public Ring(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
    }

    private void initMyView() {
        angle = 0;
        int strokeWidth = 40;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.RED);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasCenterX = w/2;
        canvasCenterY = h/2;
        rect = new RectF(canvasCenterX - 150, canvasCenterY - 150, canvasCenterX + 150, canvasCenterY + 150);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
    }


    public float getAngle() { return angle; }
    public void  setAngle(float angle) { this.angle = angle; }
}
