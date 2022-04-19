package com.thunder.thunderplane.wedgit

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.thunder.thunderplane.tool.Tool.getRandomBackground

class RandomBgView : ConstraintLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet,defStyle:Int) : super(context, attrs,defStyle) {
        initView()
    }

    private fun initView() {
        inflate(context,context.getRandomBackground(),this)
    }

}