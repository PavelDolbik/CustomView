## DraggedLayout

#### Modify layout
```java
@Override
protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);

    if (getChildCount() != 2) {
        throw new IllegalStateException("DraggedLayout must have 2 children!");
    }

    blueLayout = getChildAt(0);
    blueLayout.layout(left, top, right, bottom - 100);

    redLayout = getChildAt(1);
    if(!opened) {
        int panelMeasuredHeight = redLayout.getMeasuredHeight();
        redLayout.layout(left, bottom - 100, right, bottom - 100 + panelMeasuredHeight);
    }
}
```

#### Layout
```xml
<?xml version="1.0" encoding="utf-8"?>
<com.example.pdolbik.draggedlayout.DraggedLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/holo_blue_bright">

        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@mipmap/ic_launcher"
            android:layout_gravity="center"/>
    </FrameLayout>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/holo_red_light">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/app_name"
            android:gravity="center"/>

    </FrameLayout>

</com.example.pdolbik.draggedlayout.DraggedLayout>
```

#### Add moving
```java
@Override
public boolean onTouchEvent(MotionEvent event) {
    obtainVelocityTracker();

    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            startDragging(event);
            break;
        case MotionEvent.ACTION_MOVE:
            if (touching) {
                velocityTracker.addMovement(event);

                float translation = event.getY() - touchY;
                translation = boundTranslation(translation);
                redLayout.setTranslationY(translation);
                blueLayout.setTranslationY((opened ? -(getMeasuredHeight() - 100 - translation) : translation ));
            }
            break;
        case MotionEvent.ACTION_UP:
            isBeingDragged = false;
            touching = false;

            velocityTracker.computeCurrentVelocity(1);
            float velocityY = velocityTracker.getYVelocity();
            velocityTracker.recycle();
            velocityTracker = null;

            finishAnimateToFinalPosition(velocityY);
            break;
    }
    return true;
}

public void obtainVelocityTracker() {
    if (velocityTracker == null) {
        velocityTracker = VelocityTracker.obtain();
    }
}

public void startDragging(MotionEvent event) {
    touchY = event.getY();
    touching = true;

    obtainVelocityTracker();
    velocityTracker.addMovement(event);
}
```

#### Restricts the movement of the panel with your finger under the screen
```java
public float boundTranslation(float translation) {
    if (!opened) {
        if (translation > 0) {
            translation = 0;
        }
        if (Math.abs(translation) >= redLayout.getMeasuredHeight() - 100) {
            translation = -redLayout.getMeasuredHeight() + 100;
        }
    } else {
		if (translation < 0) {
            translation = 0;
        }
        if (translation >= redLayout.getMeasuredHeight() - 100) {
            translation = redLayout.getMeasuredHeight() - 100;
        }
    }
    return translation;
}
```

#### Add animation
```java
public void finishAnimateToFinalPosition(float velocityY) {
final boolean flinging = Math.abs(velocityY) > 0.5;

    boolean opening;
    float   distY;
    long    duration;

     if (flinging) {
        // If fling velocity is fast enough we continue the motion starting
        // with the current speed

        opening = velocityY < 0;

        distY = calculateDistance(opening);
        duration = Math.abs(Math.round(distY / velocityY));

        animatePanel(opening, distY, duration);
    } else {
        // If user motion is slow or stopped we check if half distance is
        // traveled and based on that complete the motion

        boolean halfway = Math.abs(redLayout.getTranslationY()) >= (getMeasuredHeight() - 100) / 2;
        opening = opened ? !halfway : halfway;

        distY = calculateDistance(opening);
        duration = Math.round(1000 * (double) Math.abs((double) redLayout.getTranslationY())
                / (double) (getMeasuredHeight() - 100));

    }

    animatePanel(opening, distY, duration);
}

public float calculateDistance(boolean opening) {
    float distY;
    if (opened) {
        distY = opening ? -redLayout.getTranslationY() : getMeasuredHeight() - 100 - redLayout.getTranslationY();
    } else {
        distY = opening ? -(getMeasuredHeight() - 100 + redLayout.getTranslationY()) : -redLayout.getTranslationY();
    }
    return distY;
}


public void animatePanel(final boolean opening, float distY, long duration) {
    ObjectAnimator redPanelAnimator = ObjectAnimator.ofFloat(redLayout, View.TRANSLATION_Y,
            redLayout.getTranslationY(), redLayout.getTranslationY() + distY);
    ObjectAnimator bluePanelAnimator = ObjectAnimator.ofFloat(blueLayout, View.TRANSLATION_Y,
            blueLayout.getTranslationY(), blueLayout.getTranslationY() + distY);

    AnimatorSet set = new AnimatorSet();
    set.playTogether(redPanelAnimator, bluePanelAnimator);
    set.setDuration(duration);
    set.setInterpolator(new DecelerateInterpolator());
    set.addListener(new MyAnimListener(opening));
    set.start();
}

class MyAnimListener implements Animator.AnimatorListener {

    int oldLayerTypeOne;
    int oldLayerTypeTwo;
    boolean opening;

    public MyAnimListener(boolean opening) {
        super();
        this.opening = opening;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        oldLayerTypeOne = redLayout.getLayerType();
        oldLayerTypeOne = blueLayout.getLayerType();

        redLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        blueLayout.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {}

    @Override
    public void onAnimationEnd(Animator animation) {
        setOpenedState(opening);

        blueLayout.setTranslationY(0);
        redLayout.setTranslationY(0);

        redLayout.setLayerType(oldLayerTypeOne, null);
        blueLayout.setLayerType(oldLayerTypeTwo, null);

        requestLayout();
    }

    @Override
    public void onAnimationCancel(Animator animator) {}
}
```
