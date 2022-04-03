package com.thunder.thunderplane.tool

import android.util.DisplayMetrics
import com.thunder.thunderplane.MyApplication

object UITool {

    fun getScreenHeight() : Int = MyApplication.instance.applicationContext.resources.displayMetrics.heightPixels

    fun getScreenWidth() : Int = MyApplication.instance.applicationContext.resources.displayMetrics.widthPixels

}