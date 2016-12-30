package com.dolbik.pavel.kotlinanimation.animation

import android.support.v4.view.ViewCompat
import android.view.View
import android.view.animation.Interpolator
import rx.Observable
import rx.Subscriber
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger


/** Принемает View, выполняет анимацию над ней, и помещает ее в onNext(). <br>
 *  That will receive a view, perform an animation on it, and pass it to the subscriber's onNext() method */
class TranslateViewOperator(
        val translateX:   Float,
        val duration:     Long,
        val interpolator: Interpolator) : Observable.Operator<View, View> {


    /** Количество запущенных анимаций. <br>
     *  Counts the number of animations in progress. */
    private val numberOfRunningAnimations = AtomicInteger(0)

    /** Указывает, получил этот оператор вызов onComplete() или нет. <br>
     *  Indicates whether this operator received the onComplete() call or not. */
    private val isOnCompletedCall = AtomicBoolean(false)


    override fun call(subscriber: Subscriber<in View>) = object : Subscriber<View>() {
        override fun onNext(view: View) {
            // Не запускаем анимацию, если подписчик отписан.
            // Don't start animation if the subscriber has unsubscribed.
            if (subscriber.isUnsubscribed) return

            // Запускаем анимацию.
            // Run the animation.
            numberOfRunningAnimations.incrementAndGet()
            ViewCompat.animate(view)
                    .translationX(translateX)
                    .setDuration(duration)
                    .setInterpolator(interpolator)
                    .withEndAction {
                        numberOfRunningAnimations.incrementAndGet()

                        // Если анимация завершина и подписчик все еще подписан, передаем view в onNext().
                        // The animation is done, check if the subscriber is still subscribed
                        // and pass the animated view to onNext().
                        if (!subscriber.isUnsubscribed) {
                            subscriber.onNext(view)

                            // Если приняли событие onComplete, но анимация еще выполняется - ждем её завершение.
                            // If we received the onComplete() event sometime while the animation was running,
                            // wait until all animations are done.
                            if (numberOfRunningAnimations.get() == 0 && isOnCompletedCall.get()) {
                                subscriber.onCompleted()
                            }
                        }
                    }
        }

        override fun onCompleted() {
            isOnCompletedCall.set(true)
            if (!subscriber.isUnsubscribed && numberOfRunningAnimations.get() == 0) {
                subscriber.onCompleted()
            }
        }

        override fun onError(e: Throwable) { subscriber.onError(e) }
    }


}
