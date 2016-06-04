package com.pavel.dolbik.dropview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class DropView extends ImageView {

    private Paint  imgPaint;
    private Bitmap roundBitmap;
    private Paint  whiteBorder;
    private int    whiteStrokeWidth = 1;
    private float  metrics;
    private Paint  shadow;

    int radius;
    int side;
    int halfSide;
    int avatarPadding;
    int radiusAvatar;

    private int[]   shadowGradientColorsInner;
    private float[] shadowGradientPositionsInner;

    public DropView(Context context) {
        super(context);
        initDropView();
    }

    public DropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDropView();
    }

    public DropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDropView();
    }

    private void initDropView() {
        imgPaint = new Paint();
        imgPaint.setAntiAlias(true);
        imgPaint.setFilterBitmap(true); //фильтрация будет оказывать влияние на растровое изображение при трансформации
        imgPaint.setDither(true);//для сглаживания цветов, позволяет уменьшить визуальные артефакты

        whiteBorder = new Paint();
        whiteBorder.setAntiAlias(true);
        whiteBorder.setColor(Color.WHITE);
        whiteBorder.setStrokeWidth(whiteStrokeWidth);
        whiteBorder.setStyle(Paint.Style.FILL_AND_STROKE);

        shadow = new Paint();
        shadow.setAntiAlias(true);
        shadow.setColor(Color.BLACK);
        //In Manifest android:hardwareAccelerated="false"
        //shadow.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        shadow.setShadowLayer(10.0f, 0.0f, 0.0f, Color.BLACK);
        shadowGradientColorsInner    = new int[5];
        shadowGradientPositionsInner = new float[5];
        int shadowColor = 245;
        shadowGradientColorsInner[4] = Color.argb(100, shadowColor, shadowColor, shadowColor);
        shadowGradientColorsInner[3] = Color.argb(100, shadowColor, shadowColor, shadowColor);
        shadowGradientColorsInner[2] = Color.argb(80,  shadowColor, shadowColor, shadowColor);
        shadowGradientColorsInner[1] = Color.argb(0,   shadowColor, shadowColor, shadowColor);
        shadowGradientColorsInner[0] = Color.argb(0,   shadowColor, shadowColor, shadowColor);

        shadowGradientPositionsInner[4] = 1-0.0f;
        shadowGradientPositionsInner[3] = 1-0.06f;
        shadowGradientPositionsInner[2] = 1-0.10f;
        shadowGradientPositionsInner[1] = 1-0.20f;
        shadowGradientPositionsInner[0] = 1-1.0f;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Bitmap bitmap;
        Drawable drawable = getDrawable();
        if (drawable != null) {
            bitmap = ((BitmapDrawable)drawable).getBitmap();
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
        }

        metrics       = this.getResources().getDisplayMetrics().density;
        radius        = (int) (getWidth()/2 - metrics*5);
        side          = (int) (radius * Math.sqrt(2));
        halfSide      = (side /2);
        avatarPadding = getAvatarPadding();
        radiusAvatar  = getWidth() - avatarPadding;

        roundBitmap = getRoundedCroppedBitmap(bitmap, radiusAvatar);
    }


    private int getAvatarPadding() {
        int temp = Math.round(metrics*5*2.8f);
        if (temp%2 == 0) {
            return temp;
        } else {
            return temp+1;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //Рисуем нижний теугольник (Draw the bottom triangle)
        Path path = new Path();
        path.moveTo((getWidth()/2)-halfSide, getHeight()/2+halfSide);
        path.lineTo(getWidth()/2, getHeight() - 5);
        path.lineTo(getWidth()/2+halfSide, getHeight()/2+halfSide);
        path.close();

        //Рисуем тень (Draw shadow)
        canvas.drawPath(path, shadow);
        RadialGradient shadowShaderInner = new RadialGradient(
                getWidth()/2, getHeight()/2, (getWidth()/2) - metrics*5 + 3,
                shadowGradientColorsInner,
                shadowGradientPositionsInner,
                Shader.TileMode.CLAMP);
        shadow.setShader(shadowShaderInner);
        canvas.drawCircle(getWidth()/2, getHeight()/2, (getWidth()/2) - metrics*5 + 3, shadow);

        //Рисуем треугольник (Draw a triangle )
        canvas.drawPath(path, whiteBorder);

        //Рисуем белый контур (Draw a white contour)
        canvas.drawCircle(getWidth()/2, getHeight()/2, (getWidth()/2) - metrics*5, whiteBorder);

        //Рисуем аватар (Drawing avatar)
        canvas.drawBitmap(roundBitmap, avatarPadding/2, avatarPadding/2, null);
    }

    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
        Bitmap finalBitmap = bitmap.createScaledBitmap(bitmap, radius , radius, false);
        Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(), finalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Rect rect = new Rect(0, 0, output.getWidth(), output.getHeight());

        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawCircle(
                finalBitmap.getWidth()  / 2,
                finalBitmap.getHeight() / 2,
                finalBitmap.getWidth() / 2,
                imgPaint);

        imgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(finalBitmap, rect, rect, imgPaint);

        return output;
    }
}
