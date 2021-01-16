package ru.shirobokov.reanimation.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.shirobokov.reanimation.R
import ru.shirobokov.reanimation.databinding.SwipeCardBinding

class SwipeCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: SwipeCardBinding by viewBinding()
    private var onSwipe: (() -> Unit)? = null
    private val gestureDetector = GestureDetector(context, GestureListener())

    init {
        inflate(context, R.layout.swipe_card, this)

        context.obtainStyledAttributes(attrs, R.styleable.SwipeCard).use {
            binding.title.text = it.getText(R.styleable.SwipeCard_swipe_card_title)
            binding.card.background = it.getDrawable(R.styleable.SwipeCard_swipe_card_background)
        }

        isFocusable = true
        isFocusableInTouchMode = true
        isClickable = true
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        binding.swipeLayout.isVisible = gainFocus
        binding.swipeIcon.isVisible = gainFocus
        if (gainFocus) startSwipeAnimation()
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        if (isFocused) {
            requestDisallowInterceptTouchEvent(true)
            gestureDetector.onTouchEvent(event)
        } else {
            super.onTouchEvent(event)
        }

    fun setTitle(text: String) {
        binding.title.text = text
    }

    fun setSubtitle(text: String) {
        binding.subtitle.text = text
        binding.space.isVisible = true
        binding.subtitle.isVisible = true
    }

    fun setCardColor(@DrawableRes drawableRes: Int) {
        binding.card.background = ContextCompat.getDrawable(context, drawableRes)
    }

    fun setOnSwipeListener(swipe: () -> Unit) {
        onSwipe = swipe
    }

    private fun startSwipeAnimation() {
        ValueAnimator.ofFloat(0f, width.toFloat()).apply {
            addUpdateListener { binding.swipeIcon.translationX = it.animatedValue as Float }
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.RESTART
            duration = ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }.start()
    }

    @Suppress("DEPRECATION")
    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean = true
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            if ((e2.x - e1.x) * velocityX > SWIPE_VELOCITY_THRESHOLD && isFocused) {
                requestDisallowInterceptTouchEvent(false)
                onSwipe?.invoke()
                clearFocus()
                (parent as? View)?.requestFocus()
                vibrate()
                return true
            }

            return false
        }

        private fun vibrate() {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).run {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrate(
                        VibrationEffect.createOneShot(
                            VIBRATE_TIME,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrate(VIBRATE_TIME)
                }
            }
        }
    }

    companion object {
        private const val ANIMATION_DURATION = 1000L
        private const val SWIPE_VELOCITY_THRESHOLD = 500000
        private const val VIBRATE_TIME = 100L
    }
}