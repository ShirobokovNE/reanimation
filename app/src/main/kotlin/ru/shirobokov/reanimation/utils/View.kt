package ru.shirobokov.reanimation.utils

import android.content.res.Resources
import android.view.View
import android.view.ViewTreeObserver

inline fun View.doOnGlobalLayout(crossinline action: (view: View) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action(this@doOnGlobalLayout)
        }
    })
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()