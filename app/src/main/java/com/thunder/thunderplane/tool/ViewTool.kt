package com.thunder.thunderplane.tool

import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.thunder.thunderplane.R

object ViewTool {


    fun Activity.getRandomUFOView(): View {
        val viewList = mutableListOf<Int>()
        viewList.add(R.drawable.ufo)
        viewList.add(R.drawable.ufo1)
        viewList.add(R.drawable.ufo2)
        viewList.add(R.drawable.ufo3)

        val view = View.inflate(this, R.layout.ufo_layout, null)
        val img = view.findViewById<ImageView>(R.id.ufo)
        img.setImageResource(viewList[(0 until viewList.size - 1).random()])
        return view
    }

    fun Activity.getUFoBullet(): View {
        return View.inflate(this, R.layout.ufo_bullet_layout, null)
    }

    fun Activity.getJetBullet(): View {
        return View.inflate(this, R.layout.bullet_layout, null)
    }

    fun Activity.getExplodeView():View{
        return View.inflate(this,R.layout.explode_layout,null)
    }

}