## RingAnimation

#### Init resources
```java
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
```

#### Draw ring
```java
@Override
protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawArc(rect, START_ANGLE_POINT, angle, false, paint);
}
```

#### Create animation
```java
public class RingAngleAnimation extends Animation {

	private Ring  ring;
	private float oldAngle;
	private float newAngle;

	public RingAngleAnimation(Ring ring, int newAngle) {
		this.oldAngle = ring.getAngle();
		this.newAngle = newAngle;
		this.ring     = ring;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		float angle = oldAngle + ((newAngle - oldAngle) * interpolatedTime);

		ring.setAngle(angle);
		ring.requestLayout();
	}

}
```

#### Start animation
```java
final Ring ring = (Ring) findViewById(R.id.ring);
final RingAngleAnimation ringAngleAnimation = new RingAngleAnimation(ring, 360);
ringAngleAnimation.setDuration(5000);
ring.startAnimation(ringAngleAnimation);

ringAngleAnimation.setAnimationListener(new Animation.AnimationListener() {
    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        new Handler().postDelayed(new Runnable() {
            @Override
                 public void run() {
                    ring.startAnimation(ringAngleAnimation);
                }
            }, 1000);
        }

    @Override
    public void onAnimationRepeat(Animation animation) {}
});
```