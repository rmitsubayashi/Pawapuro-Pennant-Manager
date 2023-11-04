package com.rmitsubayashi.pennantmanager.ui.util

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class ScrollResponsiveEdittext(context: Context, attrs: AttributeSet): AppCompatEditText(context, attrs) {
    private lateinit var scrollerTask: Runnable
    private var initialPosition = -1

    private val newCheckDelay = 100L

    interface OnScrollStoppedListener {
        fun onScrollStopped()
    }

    private var onScrollStoppedListener: OnScrollStoppedListener? = null

    init {
        scrollerTask = Runnable {
            val newPosition = scrollY
            if (initialPosition - newPosition == 0 ) {
                onScrollStoppedListener?.onScrollStopped()
            } else {
                initialPosition = scrollY
                postDelayed(scrollerTask, newCheckDelay)
            }
        }
    }

    fun setOnScrollStoppedListener(listener: () -> Unit) {
        onScrollStoppedListener = object : OnScrollStoppedListener {
            override fun onScrollStopped() {
                listener.invoke()
            }
        }
    }

    fun startScrollerTask() {
        initialPosition = scrollY
        postDelayed(scrollerTask, newCheckDelay)
    }
}