package com.thunder.thunderplane.tool

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import com.thunder.thunderplane.MyApplication
import com.thunder.thunderplane.R

object UITool {

    fun getScreenHeight() : Int = MyApplication.instance.applicationContext.resources.displayMetrics.heightPixels

    fun getScreenWidth() : Int = MyApplication.instance.applicationContext.resources.displayMetrics.widthPixels

    fun Activity.getBulletView() : View = View.inflate(this,R.layout.bullet_layout,null)

}