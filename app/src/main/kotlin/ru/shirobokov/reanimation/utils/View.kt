package ru.shirobokov.reanimation.utils

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView

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

@Suppress("DEPRECATION")
fun TextView.setTextAsHtml(htmlText: String?) {
    val preparedText = htmlText.orEmpty().replace("\n", "<br/>", false)
    val spannable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SpannableStringBuilder(Html.fromHtml(preparedText, Html.FROM_HTML_MODE_LEGACY))
    } else {
        SpannableStringBuilder(Html.fromHtml(preparedText))
    }

    val htmlSpans = spannable.getSpans(0, spannable.length, URLSpan::class.java)
    for (urlSpan in htmlSpans) {
        val start = spannable.getSpanStart(urlSpan)
        val end = spannable.getSpanEnd(urlSpan)
        val flags = spannable.getSpanFlags(urlSpan)
        val url = urlSpan.url

        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }, start, end, flags)

        spannable.removeSpan(urlSpan)
    }
    text = spannable
    movementMethod = LinkMovementMethod.getInstance()
}