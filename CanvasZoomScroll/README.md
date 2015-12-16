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

#### Scale canvasHeight
```java
@Override
protected void onDraw(Canvas canvas) {
    canvas.save();   
    canvas.scale(mScaleFactor, mScaleFactor);
    canvas.restore();
}
```
