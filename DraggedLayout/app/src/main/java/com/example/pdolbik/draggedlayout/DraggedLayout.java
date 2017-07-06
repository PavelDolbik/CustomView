package com.example.pdolbik.draggedlayout;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;


public class DraggedLayout extends FrameLayout {

    private View blueLayout;
    private View redLayout;

    private float   touchY;
    private int     touchSlop;
    private boolean touching;
    private boolean isBeingDragged;
    private boolean opened = false;

    private VelocityTracker velocityTracker = null;

    public DraggedLayout(Context context) {
        super(context);
        init();
    }

    public DraggedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

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

    @Override
    //Перехват touch’a у других элементов
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(touchY - event.getY()) > touchSlop) {
                    isBeingDragged = true;
                    startDragging(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                isBeingDragged = false;
                break;
        }
        return isBeingDragged;
    }


    public void startDragging(MotionEvent event) {
        touchY = event.getY();
        touching = true;

        obtainVelocityTracker();
        velocityTracker.addMovement(event);
    }


    public void obtainVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
    }


    //Отвечает за то, что бы панель не вышла за границы экрана
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

    //Если скорость движения панели высока - довести панель до конца, иначе - вернуть в исходное состояние
    //Если скорость низкая, но панель перешла середину - довести панель до конца
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
            opening = opened != halfway;

            distY = calculateDistance(opening);
            duration = Math.round(1000 * Math.abs((double) redLayout.getTranslationY())
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

    private class MyAnimListener implements Animator.AnimatorListener {

        int oldLayerTypeOne;
        int oldLayerTypeTwo;
        boolean opening;

        MyAnimListener(boolean opening) {
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

    private void setOpenedState(boolean opened) {
        this.opened = opened;
    }
}
