package com.example.pdolbik.ringanimation;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by p.dolbik on 28.12.2015.
 */
public class RingAngleAnimation extends Animation {

    private Ring ring;

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
