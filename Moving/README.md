## Moving

Moving bitmap with simple animation (fling)

#### Draw bitmap
```java
private Bitmap androidBitmap;
private Matrix androidMatrix;

public MyCustomView(Context context) {
    super(context);
	androidMatrix = new Matrix();
	androidBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.android);
}

@Override
protected void onDraw(Canvas canvas) {
    canvas.drawBitmap(androidBitmap, androidMatrix, null);
}
```

#### Add GestureDetector
```java
GestureDetector detector = new GestureDetector(context, new MyGestureListener());

private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }
}

@Override
public boolean onTouchEvent(MotionEvent event) {
    detector.onTouchEvent(event);
    return true;
}
```

#### Add moving
```java
public void onMove(float dx, float dy) {
    androidMatrix.postTranslate(dx, dy);
    invalidate();
}

public void onResetLocation() {
    androidMatrix.reset();
    androidMatrix.postTranslate(centerX, centerY);
    invalidate();
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
		return true;
    }
}
```

#### Add animation when fling
```java
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
```

