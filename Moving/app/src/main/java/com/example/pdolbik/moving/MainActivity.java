package com.example.pdolbik.moving;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    private FrameLayout  frameLayout;
    private MyCustomView myCustomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout  = (FrameLayout) findViewById(R.id.container);
        myCustomView = new MyCustomView(this);

        frameLayout.addView(myCustomView);
    }
}
