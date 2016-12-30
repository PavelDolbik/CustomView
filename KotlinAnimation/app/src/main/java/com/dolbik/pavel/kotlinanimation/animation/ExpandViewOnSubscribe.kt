package com.dolbik.pavel.kotlinanimation.animation

import android.support.design.widget.FloatingActionButton
import android.support.v4.view.ViewCompat
import android.view.animation.Interpolator
import rx.Completable
import java.util.concurrent.atomic.AtomicInteger

class ExpandViewOnSubscribe(
        val views:         List<FloatingActionButton>,
        val animationType: String,
        val duration:      Long,
        val interpolator: Interpolator,
        val paddingPx:     Int) : Completable.CompletableOnSubscribe {


    companion object {
        const val EXPAND_HORIZONTALLY   = "expand_horizontally"
        const val COLLAPSE_HORIZONTALLY = "collapse_horizontally"
        const val EXPAND_VERTICALLY     = "expand_vertically"
        const val COLLAPSE_VERTICALLY   = "collapse_vertically"
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

        val fabWidth  = views[0].width
        val fabHeight = views[0].height

        val horizontalExpansion = animationType == EXPAND_HORIZONTALLY
        val verticalExpansion   = animationType == EXPAND_VERTICALLY

        val xTranslateFactor = if (horizontalExpansion) fabWidth  else 0
        val yTranslateFactor = if (verticalExpansion)   fabHeight else 0

        val paddingX = if (horizontalExpansion) paddingPx else 0
        val paddingY = if (verticalExpansion)   paddingPx else 0

        for (i in views.indices) {
            ViewCompat.animate(views[i])
                    .translationX(i * (xTranslateFactor.toFloat() + paddingX))
                    .translationY(i * (yTranslateFactor.toFloat() + paddingY))
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