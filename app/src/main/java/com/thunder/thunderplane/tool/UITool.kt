package com.thunder.thunderplane.tool

import android.content.Context
import android.util.TypedValue
import com.thunder.thunderplane.MyApplication
import com.thunder.thunderplane.R
import kotlin.math.roundToInt

object UITool {

    fun getScreenHeight(): Int =
        MyApplication.instance.applicationContext.resources.displayMetrics.heightPixels

    fun getScreenWidth(): Int =
        MyApplication.instance.applicationContext.resources.displayMetrics.widthPixels

    fun Context.getRandomBackground(): Int {
        val viewList = mutableListOf<Int>()
        viewList.add(R.layout.item_background_layout)
        viewList.add(R.layout.item_background_layout1)
        viewList.add(R.layout.item_background_layout2)
        return viewList[(0 until viewList.size).random()]
    }

    fun Context.getPixel(dp: Int) : Int {
       return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            this.resources.displayMetrics
        ).roundToInt()
    }

}