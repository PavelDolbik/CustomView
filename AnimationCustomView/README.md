## AnimationCustomView

#### Init resources
```java
public MyView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initMyView();
}

private void initMyView() {
	float radius = 20.0f;
    CornerPathEffect cornerEffect = new CornerPathEffect(radius);
    trapezePath trapezePath = new Path();

    Paint eraser = new Paint();
    eraser.setAntiAlias(true);
    eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

    Paint innerCirclePain = new Paint();
    innerCirclePain.setColor(Color.BLUE);
    innerCirclePain.setAntiAlias(true);
    innerCirclePain.setStyle(Paint.Style.FILL);

    Paint trapezePain = new Paint();
    trapezePain.setColor(Color.BLUE);
    trapezePain.setAntiAlias(true);
    trapezePain.setStyle(Paint.Style.FILL);
    trapezePain.setPathEffect(cornerEffect);

    initFadeOut();
    initFadeIn();
    }
```

#### Init animation 
```java
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
```

#### Draw view
```java
@Override
protected void onDraw(Canvas canvas) {
    int radius = 130;

    bitmapTransp.eraseColor(Color.TRANSPARENT);
    bitmapCanvas.drawColor(Color.BLUE);
    //Transparent circle
    bitmapCanvas.drawCircle(canvasCenterX, canvasCenterY, radius, eraser);

    //Blue rectangle
    Rect rectangle = new Rect(canvasCenterX - 150, canvasCenterY - 150, canvasCenterX + 150, canvasCenterY + 150);
    canvas.drawBitmap(bitmapTransp, rectangle, rectangle, null);

    //The inner blue circle
    canvas.drawCircle(canvasCenterX, canvasCenterY, 110, innerCirclePain);

    //Draw a trapezoid
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
```
