package com.thunder.thunderplane.tool

import android.animation.ValueAnimator
import android.app.ActionBar
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import com.thunder.thunderplane.MyApplication
import com.thunder.thunderplane.R
import com.thunder.thunderplane.log.MichaelLog
import java.time.Duration
import kotlin.math.roundToInt

object Tool {

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

    fun Context.getPixel(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            this.resources.displayMetrics
        ).roundToInt()
    }

    fun isCreateUpgradeItem(): Boolean {
        val randomNumber = (1..100).random()
        MichaelLog.i("randomNum : $randomNumber")
        var isCreate = false
        for (number in getRandomList()) {
            if (number == randomNumber) {
                MichaelLog.i("number : $number random $randomNumber")
                isCreate = true
                break
            }
        }
        return isCreate
    }

    private fun getRandomList(): MutableList<Int> {
        val list = mutableListOf<Int>()
//        for (i in 2..sqrt(100.0).toInt()){
//            if (100 % i == 0){
//                list.add(i)
//            }
//        }
        for (i in 1..100) {
            if (i % 2 == 0) {
                list.add(i)
            }
        }
        MichaelLog.i("list size : ${list.size}")
        return list
    }

    fun expend(view : View,duration: Long , targetHeight : Int){
        val prevHeight = view.height
        view.visibility = View.VISIBLE
        val valueAnimator = ValueAnimator.ofInt(prevHeight,targetHeight)
        valueAnimator.addUpdateListener {
            view.layoutParams.height = it.animatedValue as Int
            view.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration
        valueAnimator.start()
    }

}