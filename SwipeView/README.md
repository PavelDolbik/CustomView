## SwipeView

#### Init resources
```java
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
```

#### Create GestureListener
```java
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
```

#### Init Detector
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    detector.onTouchEvent(event);
    return true;
}
```

#### Add animation
```java
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
```

