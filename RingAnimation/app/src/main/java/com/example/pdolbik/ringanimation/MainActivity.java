package com.example.pdolbik.ringanimation;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
}
