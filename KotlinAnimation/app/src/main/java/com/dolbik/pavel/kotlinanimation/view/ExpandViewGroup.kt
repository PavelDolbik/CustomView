package com.dolbik.pavel.kotlinanimation.view

import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.dolbik.pavel.kotlinanimation.animation.ExpandViewOnSubscribe
import com.dolbik.pavel.kotlinanimation.R
import com.dolbik.pavel.kotlinanimation.animation.ScaleViewOnSubscribe
import rx.Completable

class ExpandViewGroup: ViewGroup {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private val INTERPOLATOR = AccelerateDecelerateInterpolator()
    private val DURATION_MS  = 300L
    private val PADDING_PX   = 32


    private var currentItems = mutableListOf<FloatingActionButton>()

    init {
        for (i in 1..7) {
            val fab = FloatingActionButton(this.context)
            fab.apply {
                size = FloatingActionButton.SIZE_MINI
                compatElevation = 0f
                setImageResource(R.drawable.abc_ic_arrow_drop_right_black_24dp)
                scaleX = 0f
                scaleY = 0f
            }
            currentItems.add(fab)
            addView(fab)
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var childState = 0
        var maxWidth   = 0
        var maxHeight  = 120

        for (i in 0..childCount - 1) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                childState = View.combineMeasuredStates(childState, child.measuredState)
                maxWidth  += child.measuredWidth
                maxHeight = Math.max(maxHeight, child.measuredHeight)
            }
        }

        setMeasuredDimension(
                View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState shl View.MEASURED_HEIGHT_STATE_SHIFT))

    }


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val childLeft   = this.paddingLeft
        val childTop    = this.paddingTop
        val childRight  = this.measuredWidth - this.paddingRight
        val childBottom = this.measuredHeight - this.paddingBottom
        val childWidth  = childRight - childLeft
        val childHeight = childBottom - childTop
        for (i in 0..childCount - 1) {
            val child = getChildAt(i)

            if (child.visibility == View.GONE) { return }

            child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST))
            child.layout(childLeft, childTop, childLeft + child.measuredWidth, childTop + child.measuredHeight)
        }
    }



    fun expandHorizontally(items: MutableList<FloatingActionButton>): Completable
            = Completable.create(ExpandViewOnSubscribe(items, ExpandViewOnSubscribe.EXPAND_HORIZONTALLY, DURATION_MS, INTERPOLATOR, PADDING_PX))

    fun collapseHorizontally(items: MutableList<FloatingActionButton>): Completable
            = Completable.create(ExpandViewOnSubscribe(items, ExpandViewOnSubscribe.COLLAPSE_HORIZONTALLY, DURATION_MS, INTERPOLATOR, PADDING_PX))

    fun scaleUp(items: MutableList<FloatingActionButton>) : Completable =
            Completable.create(ScaleViewOnSubscribe(items, ScaleViewOnSubscribe.SCALE_UP, DURATION_MS, INTERPOLATOR))

    fun scaleDown(items: MutableList<FloatingActionButton>) : Completable =
            Completable.create(ScaleViewOnSubscribe(items, ScaleViewOnSubscribe.SCALE_DOWN, DURATION_MS, INTERPOLATOR))



    fun startAnimation() {
        scaleUp(currentItems)
                .andThen(expandHorizontally(currentItems))
                .subscribe()
    }

    fun revertAnimation() {
        scaleDown(currentItems)
                .ambWith(collapseHorizontally(currentItems))
                .subscribe()
    }

}
