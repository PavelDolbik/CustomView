package com.pavel.dolbik.lottieanimation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LottieAnimationView animationView = (LottieAnimationView) findViewById(R.id.animation_view);
        animationView.setAnimation("logo.json");

        Button playBtn = (Button) findViewById(R.id.play);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animationView.playAnimation();
            }
        });

    }
}
