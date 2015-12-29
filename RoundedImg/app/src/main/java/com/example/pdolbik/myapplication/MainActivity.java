package com.example.pdolbik.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        myView = (MyView) findViewById(R.id.img);
        myView.setImageBitmap(img);
    }
}
