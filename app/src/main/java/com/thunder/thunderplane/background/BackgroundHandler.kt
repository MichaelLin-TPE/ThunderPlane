package com.thunder.thunderplane.background

import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import com.thunder.thunderplane.base.MyApplication
import com.thunder.thunderplane.bean.BgData
import com.thunder.thunderplane.tool.Tool
import com.thunder.thunderplane.user.JetHandler
import com.thunder.thunderplane.wedgit.NotScrollNestedScrollView
import com.thunder.thunderplane.wedgit.RandomBgView

class BackgroundHandler(private val jetHandler: JetHandler) {


    private lateinit var handler : Handler

    private fun getContext(): Context {
        return MyApplication.instance.applicationContext
    }


    fun startToMoveBackground(bgRoot: LinearLayout, scrollView: NotScrollNestedScrollView , onBackgroundFinishListener: OnBackgroundFinishListener) {
        handler.post {
            val bgList = ArrayList<BgData>()
            for (i in 0..200) {
                val view = RandomBgView(getContext())

                bgRoot.addView(view)

                view.post {
                    viewSetting(view)
                    view.visibility = View.INVISIBLE
                    val data = BgData(view, view.x, view.y)
                    bgList.add(data)
                    if (bgList.size == 201) {
                        scrollView.scrollTo(0, bgList[bgList.size - 1].y.toInt())
                        bgList.forEach {
                            it.view.visibility = View.VISIBLE
                        }
                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                if (jetHandler.isGameOver) {
                                    handler.removeCallbacks(this)
                                    return
                                }
                                bgRoot.y = bgRoot.y + 5f

                                handler.postDelayed(this, 1)
                            }
                        }, 1)
                        onBackgroundFinishListener.onFinish()
                    }
                }
            }

        }
    }

    private fun viewSetting(view: View) {
        val layoutParams = view.layoutParams
        layoutParams.height = Tool.getScreenHeight()
        layoutParams.width = Tool.getScreenWidth()
        view.layoutParams = layoutParams
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }

    fun interface OnBackgroundFinishListener{
        fun onFinish()
    }

}