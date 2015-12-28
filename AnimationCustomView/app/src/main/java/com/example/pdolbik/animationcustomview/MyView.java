package com.example.pdolbik.animationcustomview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by p.dolbik on 23.12.2015.
 */
public class MyView extends View {

    private Bitmap bitmapTransp;
    private Canvas bitmapCanvas;
    private Paint  eraser;

    private int canvasCenterX;
    private int canvasCenterY;

    private CornerPathEffect cornerEffect;
    private Path  trapezePath;
    private Paint innerCirclePain;
    private Paint trapezePain;
    private float radius = 20.0f;

    private Transformation mTransformation;
    private AlphaAnimation mFadeOut;
    private Transformation mTransformationIn;
    private AlphaAnimation mFadeIn;



    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMyView();
    }


    private void initMyView() {
        cornerEffect = new CornerPathEffect(radius);
        trapezePath = new Path();

        eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        innerCirclePain = new Paint();
        innerCirclePain.setColor(Color.BLUE);
        innerCirclePain.setAntiAlias(true);
        innerCirclePain.setStyle(Paint.Style.FILL);

        trapezePain = new Paint();
        trapezePain.setColor(Color.BLUE);
        trapezePain.setAntiAlias(true);
        trapezePain.setStyle(Paint.Style.FILL);
        trapezePain.setPathEffect(cornerEffect);

        initFadeOut();
        initFadeIn();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);

        bitmapTransp = Bitmap.createBitmap(parentWidth, parentHeight, Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmapTransp);

        canvasCenterX = parentWidth/2;
        canvasCenterY = parentHeight/2;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int radius = 130;

        bitmapTransp.eraseColor(Color.TRANSPARENT);
        bitmapCanvas.drawColor(Color.BLUE);
        //Позицианируем круг в центре
        bitmapCanvas.drawCircle(canvasCenterX, canvasCenterY, radius, eraser);

        //Рисуем квадрат в центре
        Rect rectangle = new Rect(canvasCenterX - 150, canvasCenterY - 150, canvasCenterX + 150, canvasCenterY + 150);
        canvas.drawBitmap(bitmapTransp, rectangle, rectangle, null);

        //Рисуем внутренний круг
        canvas.drawCircle(canvasCenterX, canvasCenterY, 110, innerCirclePain);

        //Рисуем трапецию
        int x1 = canvasCenterX + 150 + 20;
        int y1 = canvasCenterY - 50;
        int x2 = x1 + 150;
        int y2 = y1 - 100;
        int y3 = y2 + 300;

        trapezePath.moveTo(x1, y1);
        trapezePath.lineTo(x2, y2);
        trapezePath.lineTo(x2, y3);
        trapezePath.lineTo(x1, y3 - 100);
        trapezePath.lineTo(x1, y1);

        canvas.drawPath(trapezePath, trapezePain);

        if (mFadeOut.hasStarted() && !mFadeOut.hasEnded()) {
            mFadeOut.getTransformation(System.currentTimeMillis(), mTransformation);
            innerCirclePain.setAlpha((int) ( 255 * mTransformation.getAlpha()));
            invalidate();
        }

        if (mFadeIn.hasStarted() && !mFadeIn.hasEnded()) {
            mFadeIn.getTransformation(System.currentTimeMillis(), mTransformationIn);
            innerCirclePain.setAlpha((int) ( 255 - (255 * mTransformationIn.getAlpha()) ));
            invalidate();
        } else {
            invalidate();
        }
    }


    private void initFadeOut() {
        mTransformation = new Transformation();
        mFadeOut = new AlphaAnimation(1f, 0f);
        mFadeOut.setDuration(800);
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFadeIn.start();
                        mFadeIn.getTransformation(System.currentTimeMillis(), mTransformationIn);
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        mFadeOut.start();
        mFadeOut.getTransformation(System.currentTimeMillis(), mTransformation);
    }


    private void initFadeIn() {
        mTransformationIn = new Transformation();
        mFadeIn = new AlphaAnimation(1f, 0f);
        mFadeIn.setDuration(800);
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFadeOut.start();
                        mFadeOut.getTransformation(System.currentTimeMillis(), mTransformation);
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

}
