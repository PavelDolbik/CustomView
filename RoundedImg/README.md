## RoundedImg

#### Init resources
```java
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
```

#### Get drawable
```java
@Override
protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);
    increment = (w * incrementFactor)/100;
    bitmapPosition = increment /2;

    Drawable drawable = getDrawable();
    if (drawable != null) {
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        roundBitmap = getRoundedCroppedBitmap(bitmap, getWidth() - increment);
    } else {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        roundBitmap = getRoundedCroppedBitmap(bitmap, getWidth() - increment);
    }
}
```

#### Rounded bitmap
```java
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
```

#### Draw view
```java
@Override
protected void onDraw(Canvas canvas) {
    canvas.drawBitmap(roundBitmap, bitmapPosition, bitmapPosition, null);
    if (imgSelected) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2 - redStrokeWidth, redBorder);
    }
}
```

#### Add animation
```java
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
```

#### Anim
```xml
<?xml version="1.0" encoding="utf-8"?>
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXScale="1.0"
    android:toXScale="0.9"
    android:fromYScale="1.0"
    android:toYScale="0.9"
    android:duration="200"
    android:pivotX="50%"
    android:pivotY="50%"
    android:fillAfter="true">
</scale>

<?xml version="1.0" encoding="utf-8"?>
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXScale="1.0"
    android:toXScale="1.1"
    android:fromYScale="1.0"
    android:toYScale="1.1"
    android:duration="200"
    android:pivotX="50%"
    android:pivotY="50%"
    android:fillAfter="true">
</scale>
```