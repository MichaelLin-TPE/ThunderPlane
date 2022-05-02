package com.thunder.thunderplane

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.thunder.thunderplane.base.MyApplication
import com.thunder.thunderplane.bean.UfoBossData
import com.thunder.thunderplane.bean.UfoBulletData
import com.thunder.thunderplane.tool.Tool
import com.thunder.thunderplane.tool.ViewTool.getBossBullet
import com.thunder.thunderplane.tool.ViewTool.getSmallBoss

class SmallBossHandler(val jetHandler: JetHandler,val ufoHandler: UFOHandler) {

    private lateinit var root: ConstraintLayout
    val ufoBossList = ArrayList<UfoBossData>()
    private lateinit var handler: Handler
    private var bossIndex = 0

    private fun getContext(): Context {
        return MyApplication.instance.applicationContext
    }

    /**
     * 創造BOSS
     */
    fun createSmallBoss(root : ConstraintLayout) {
        this.root = root
        val view = getContext().getSmallBoss()
        root.addView(view)
        view.visibility = View.INVISIBLE
        view.post {
            view.x =
                (0..(Tool.getScreenWidth() - (view.right - view.left))).random().toFloat()
            view.y = 100f
            view.tag = bossIndex
            bossIndex++
            view.visibility = View.VISIBLE
            val ufoBossData = UfoBossData(view, isRight = true, isTop = false)
            ufoBossList.add(ufoBossData)
            moveBoss(ufoBossData)
            bossShootUser(view)
        }
    }

    private fun bossShootUser(view: View) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (jetHandler.isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                if (isUFOBossDestroy(view.tag)) {
                    handler.removeCallbacks(this)
                    return
                }
                val bullet = getContext().getBossBullet()
                root.addView(bullet)
                bullet.visibility = View.INVISIBLE
                bullet.post {
                    bullet.x =
                        (view.x + ((view.right - view.left) / 2)) - ((bullet.right - bullet.left) / 2)
                    bullet.y = view.y + (view.bottom - view.top)
                    bullet.visibility = View.VISIBLE
                    val data = UfoBulletData(bullet)
                    ufoHandler.ufoBulletList.add(data)
                    ufoHandler.moveUFOBullet(data)
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 3000)
            }

        }, 3000)
    }

    /**
     * 確認 BOSS是否清除
     */
    private fun isUFOBossDestroy(tag: Any): Boolean {
        if (ufoBossList.isEmpty()) {
            return true
        }
        var isDestroy = false
        ufoBossList.forEach {
            if (tag == it.boss.tag) {
                isDestroy = false
            }
        }
        return isDestroy
    }

    /**
     * 移動BOSS
     */
    private fun moveBoss(data: UfoBossData) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (jetHandler.isGameOver) {
                    root.removeView(data.boss)
                    handler.removeCallbacks(this)
                    return
                }
                if (data.isRight) {
                    data.boss.x = data.boss.x + 5f
                } else {
                    data.boss.x = data.boss.x - 5f
                }
                if ((data.boss.x + (data.boss.right - data.boss.left)) >= Tool.getScreenWidth()) {
                    data.isRight = false
                }
                if (data.boss.x <= 0) {
                    data.isRight = true
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1)
            }
        }, 1)

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (jetHandler.isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }

                if (!data.isTop) {
                    data.boss.y = data.boss.y + 10f
                } else {
                    data.boss.y = data.boss.y - 10f
                }
                if ((data.boss.y + (data.boss.bottom - data.boss.top)) >= Tool.getScreenHeight() / 4) {
                    data.isTop = true
                }
                if (data.boss.y <= 0) {
                    data.isTop = false
                }

                handler.postDelayed(this, 1)
            }
        }, 1)
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }
}