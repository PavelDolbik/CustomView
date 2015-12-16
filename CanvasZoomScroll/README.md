## CanvasZoomScroll

Pinch zoom and scroll

### Pinch
#### Create MyScaleGestureListener
```java
private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	@Override
    public boolean onScale(ScaleGestureDetector detector) {
		float scaleFactor = scaleGestureDetector.getScaleFactor();

		float focusX = scaleGestureDetector.getFocusX();
		float focusY = scaleGestureDetector.getFocusY();

		if(mScaleFactor*scaleFactor > 1 && mScaleFactor*scaleFactor < 3){
			mScaleFactor *= scaleGestureDetector.getScaleFactor();
				   
			int scrollX = (int)((getScrollX()+ focusX) * scaleFactor - focusX);
			int scrollY = (int)((getScrollY()+ focusY) * scaleFactor - focusY);
					
			scrollX = Math.min( Math.max(scrollX, 0), (int) (canvasWidth * mScaleFactor) - canvasWidth);
			scrollY = Math.min( Math.max(scrollY, 0), (int) (canvasHeight * mScaleFactor) - canvasHeight);
					
			scrollTo(scrollX, scrollY);
		}
		
		invalidate(); 
		
		return true;
    }
}
```

#### Create ScaleGestureDetector
```java
ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(context, new MyScaleGestureListener());
```

#### Register ScaleGestureDetector
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    scaleGestureDetector.onTouchEvent(event);
    return true;
}
```

#### Scale canvas
```java
@Override
protected void onDraw(Canvas canvas) {
    canvas.save();   
    canvas.scale(mScaleFactor, mScaleFactor);
    canvas.restore();
}
```

### Scroll
#### Create MyGestureListener
```java
private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
          
        if( getScrollX() + distanceX < (canvasWidth * mScaleFactor) - canvasWidth && getScrollX() + distanceX > 0){
            scrollBy((int) distanceX, 0);
        }
			
        if( getScrollY() + distanceY < (canvasHeight * mScaleFactor) - canvasHeight && getScrollY()+ distanceY > 0){
            scrollBy(0, (int)distanceY);
        }

        if (!awakenScrollBars()) { invalidate(); }

        return true;
    }
}
```

#### Create GestureDetector
```java
GestureDetector detector = new GestureDetector(context, new MyGestureListener());
```

#### Register GestureDetector
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    detector.onTouchEvent(event);
    return true;
}
```

#### Override methods
```java
@Override
protected int computeHorizontalScrollExtent() { return canvasWidth; }
@Override
protected int computeVerticalScrollExtent() { return canvasHeight; }

@Override
protected int computeHorizontalScrollOffset() { return getScrollX(); }
@Override
protected int computeVerticalScrollOffset() { return getScrollY(); }

@Override
protected int computeHorizontalScrollRange() { return (int) (canvasWidth * mScaleFactor);}
@Override
protected int computeVerticalScrollRange() { return (int) (canvasHeight * mScaleFactor); }
```

#### Set enable scrollBar
```java
setHorizontalScrollBarEnabled(true);
setVerticalScrollBarEnabled(true);
```

#### Add attr 
```java
android:scrollbars="horizontal|vertical"
```


