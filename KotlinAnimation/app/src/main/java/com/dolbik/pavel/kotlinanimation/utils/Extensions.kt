package com.dolbik.pavel.kotlinanimation.utils

import android.view.View
import android.view.animation.Interpolator
import com.dolbik.pavel.kotlinanimation.animation.TranslateViewOperator
import rx.Observable

fun Observable<View>.translateView(
                        translationX: Float,
                        duration: Long,
                        interpolator: Interpolator) : Observable<View> =
        lift<View> (TranslateViewOperator(translationX, duration,interpolator)
)
