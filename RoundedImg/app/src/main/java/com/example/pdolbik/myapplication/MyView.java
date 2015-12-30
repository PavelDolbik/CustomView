package com.example.pdolbik.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by p.dolbik on 29.12.2015.
 */
public class MyView extends ImageView {

    private int     redStrokeWidth  = 10;
    private int     decrementFactor = 15;
    private int     decrement;
    private float   bitmapPosition;
    private Paint   redBorder;
    private Paint   imgPaint;
    private Bitmap  roundBitmap;
    private boolean imgSelected;



    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        redBorder = new Paint();
        redBorder.setAntiAlias(true);
        redBorder.setColor(Color.RED);
        redBorder.setStrokeWidth(redStrokeWidth);
        redBorder.setStyle(Paint.Style.STROKE);

        imgPaint = new Paint();
        imgPaint.setAntiAlias(true);
        imgPaint.setFilterBitmap(true);
        imgPaint.setDither(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        decrement = (w * decrementFactor)/100;
        bitmapPosition = decrement /2;

        Drawable drawable = getDrawable();
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            roundBitmap = getRoundedCroppedBitmap(bitmap, getWidth() - decrement);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            roundBitmap = getRoundedCroppedBitmap(bitmap, getWidth() - decrement);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(roundBitmap, bitmapPosition, bitmapPosition, null);
        if (imgSelected) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - redStrokeWidth, redBorder);
        }
    }


    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap = bitmap.createScaledBitmap(bitmap, radius , radius, false);
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());

        Canvas canvas = new Canvas(output);
        canvas.drawCircle(
                finalBitmap.getWidth()  / 2,
                finalBitmap.getHeight() / 2,
                finalBitmap.getWidth() / 2,
                imgPaint);

        imgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, imgPaint);

        return output;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Animation scale = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
                startAnimation(scale);
                break;
            case MotionEvent.ACTION_UP:
                imgSelected = !imgSelected;
                invalidate();
                Animation scale2 = AnimationUtils.loadAnimation(getContext(), R.anim.scale_2);
                startAnimation(scale2);
                break;
        }
        return true;
    }
}
