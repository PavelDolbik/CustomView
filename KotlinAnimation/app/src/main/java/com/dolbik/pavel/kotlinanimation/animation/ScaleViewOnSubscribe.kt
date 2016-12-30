package com.dolbik.pavel.kotlinanimation.animation

import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.view.animation.Interpolator
import rx.Completable
import java.util.concurrent.atomic.AtomicInteger

class ScaleViewOnSubscribe(
        val views:         List<FloatingActionButton>,
        val animationType: String,
        val duration:      Long,
        val interpolator:  Interpolator) : Completable.CompletableOnSubscribe {


    companion object {
        const val SCALE_UP   = "scale_up"
        const val SCALE_DOWN = "scale_down"
    }


    lateinit private var numberOfAnimationsToRun: AtomicInteger


    override fun call(subscriber: Completable.CompletableSubscriber) {
        if (views.isEmpty()) {
            subscriber.onCompleted()
            return
        }

        // Нужно запустить столько анимаций, сколько view.
        // We need to run as much as animations as there are views.
        numberOfAnimationsToRun = AtomicInteger(views.size)

        val scale = if (animationType == SCALE_UP) 1f else 0f

        for (i in views.indices) {
            ViewCompat.animate(views[i])
                    .scaleX(scale)
                    .scaleY(scale)
                    .setDuration(duration)
                    .setInterpolator(interpolator)
                    .withEndAction {
                        // Как только вся анимация закончилась, вызываем onCompleted().
                        // Once all animations are done, call onCompleted().
                        if (numberOfAnimationsToRun.decrementAndGet() == 0) {
                            subscriber.onCompleted()
                        }
                    }
        }
    }

}
