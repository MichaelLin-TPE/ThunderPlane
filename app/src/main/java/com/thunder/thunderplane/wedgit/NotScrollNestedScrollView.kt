package com.thunder.thunderplane.wedgit

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class NotScrollNestedScrollView : NestedScrollView {

    private val isScrollable = false

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {

    }

    constructor(context: Context, attrs: AttributeSet, defStyle:Int) : super(context, attrs,defStyle) {

    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isScrollable && super.onTouchEvent(ev)
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return isScrollable && super.onInterceptHoverEvent(event)
    }
}