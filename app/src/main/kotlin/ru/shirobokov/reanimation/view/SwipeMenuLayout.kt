package ru.shirobokov.reanimation.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.animation.addListener
import kotlin.math.abs

@SuppressLint("ClickableViewAccessibility")
class SwipeMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    style: Int = 0
) : LinearLayout(context, attrs, style) {

    var menuViewWidth = 0

    var onSwipeListener: ((Boolean) -> Unit)? = null
    var onClickListener: (() -> Unit)? = null

    private lateinit var mainView: View
    private lateinit var menuView: View

    private var positionOnActionDown = 0f
    private var viewNewPosition = 0

    private var isViewMoved = false
    private var isSwiped = false

    init {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> actionDown(event.x)
                MotionEvent.ACTION_UP -> actionUp(event.x)
                MotionEvent.ACTION_MOVE -> actionMove(event.x)
                MotionEvent.ACTION_CANCEL -> actionCancel()
                else -> false
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        menuView = getChildAt(MENU_VIEW_INDEX) as View
        mainView = getChildAt(MAIN_VIEW_INDEX) as View
    }

    private fun actionDown(position: Float): Boolean {
        positionOnActionDown = position
        isViewMoved = false
        return true
    }

    private fun actionUp(position: Float): Boolean {
        if (abs(position - positionOnActionDown) < SWIPE_THRESHOLD && !isViewMoved) onClickListener?.invoke()
        finishSwipe()
        return true
    }

    private fun actionMove(position: Float): Boolean {
        val distanceMove = if (isSwiped) position - positionOnActionDown else positionOnActionDown - position
        if (distanceMove > SWIPE_THRESHOLD && !isViewMoved) {
            requestDisallowInterceptTouchEvent(true)
            isViewMoved = true
        }
        calculateViewNewPosition(distanceMove)
        return true
    }

    private fun actionCancel(): Boolean {
        finishSwipe()
        return true
    }

    private fun finishSwipe() {
        val animatedValue = if (viewNewPosition > menuView.width / 2) {
            intArrayOf(viewNewPosition, menuView.width)
        } else {
            intArrayOf(viewNewPosition, 0)
        }

        ObjectAnimator
            .ofInt(*animatedValue)
            .apply {
                duration = SWIPE_ANIMATE_TIME
                addListener(
                    onEnd = {
                        isSwiped = viewNewPosition > menuView.width / 2
                        onSwipeListener?.invoke(isSwiped)
                        requestDisallowInterceptTouchEvent(false)
                    })
                addUpdateListener {
                    viewNewPosition = it.animatedValue as Int
                    mainView.translationX = -viewNewPosition.toFloat()
                    menuView.translationX = -viewNewPosition.toFloat()
                }
            }.start()
    }

    private fun calculateViewNewPosition(distanceMove: Float) {
        viewNewPosition = if (isSwiped) {
            when {
                distanceMove < 0 -> menuView.width
                distanceMove > menuView.width -> 0
                else -> menuView.width - distanceMove.toInt()
            }
        } else {
            when {
                distanceMove < 0 -> 0
                distanceMove > menuView.width -> menuView.width
                else -> distanceMove.toInt()
            }
        }
        mainView.translationX = -viewNewPosition.toFloat()
        menuView.translationX = -viewNewPosition.toFloat()
    }

    fun updateSwipedState(newIsSwiped: Boolean) {
        isSwiped = newIsSwiped
        viewNewPosition = if (newIsSwiped) menuViewWidth else 0
        mainView.translationX = -viewNewPosition.toFloat()
        menuView.translationX = -viewNewPosition.toFloat()
    }

    fun closeSwipe() {
        ObjectAnimator
            .ofInt(menuView.width, 0)
            .apply {
                duration = SWIPE_ANIMATE_TIME
                addListener(
                    onEnd = {
                        isSwiped = false
                        onSwipeListener?.invoke(isSwiped)
                    })
                addUpdateListener {
                    viewNewPosition = it.animatedValue as Int
                    mainView.translationX = -viewNewPosition.toFloat()
                    menuView.translationX = -viewNewPosition.toFloat()
                }
            }.start()
    }

    companion object {
        private const val SWIPE_THRESHOLD = 10
        private const val SWIPE_ANIMATE_TIME = 200L
        private const val MAIN_VIEW_INDEX = 0
        private const val MENU_VIEW_INDEX = 1
    }
}