package com.dolbik.pavel.kotlinanimation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.dolbik.pavel.kotlinanimation.utils.translateView
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable
import rx.Subscription

class MainActivity : AppCompatActivity() {


    private val metric by lazy { resources.displayMetrics }
    private var animationSbs: Subscription? = null
    private var isStartAnimation = true




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            if (isStartAnimation) {
                button.text = resources.getString(R.string.reverse)
                startAnimation()
                expandGroup.startAnimation()
                isStartAnimation = false
            } else {
                button.text = resources.getString(R.string.animate)
                reverseAnimation()
                expandGroup.revertAnimation()
                isStartAnimation = true
            }
        }
    }


    fun startAnimation() {
        animationSbs?.unsubscribe()
        animationSbs = Observable
                .just(circle as View)
                .translateView((metric.widthPixels - circle.width).toFloat(), 600, DecelerateInterpolator())
                .subscribe()
    }


    fun reverseAnimation() {
        animationSbs?.unsubscribe()
        animationSbs = Observable
                .just(circle as View)
                .translateView(0f, 600, DecelerateInterpolator())
                .subscribe()
    }
}
