package com.thunder.thunderplane

import android.content.Context
import android.os.Looper
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.thunder.thunderplane.base.MyApplication
import com.thunder.thunderplane.bean.UFOData
import com.thunder.thunderplane.bean.UfoBossData
import com.thunder.thunderplane.bean.UfoBulletData
import com.thunder.thunderplane.tool.MusicTool
import com.thunder.thunderplane.tool.Tool
import com.thunder.thunderplane.tool.ViewTool.getRandomUFOView
import com.thunder.thunderplane.tool.ViewTool.getUFoBullet

class UFOHandler(val jetHandler: JetHandler,val bigBossHandler: BigBossHandler) {

    private var ufoIndex = 0
    private val handler = android.os.Handler(Looper.myLooper()!!)
    val ufoList = ArrayList<UFOData>()
    private lateinit var root: ConstraintLayout
    private val ufoBulletList = ArrayList<UfoBulletData>()
    private lateinit var onShowGameOverListener: OnShowGameOverListener

    public fun setOnShowGameOverListener(showGameOverListener: OnShowGameOverListener){
        onShowGameOverListener = showGameOverListener
    }

    private fun getContext() : Context {
        return MyApplication.instance.applicationContext
    }

    /**
     * 開始產生UFO
     */
    fun appearUfo(root : ConstraintLayout){
        this.root = root
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (jetHandler.isGameOver || bigBossHandler.bigBossData != null) {
                    clearAllUFO(root)
                    clearUpgradeItem()
                    handler.removeCallbacks(this)
                    return
                }
                val ufo = getContext().getRandomUFOView()
                root.addView(ufo)
                ufo.visibility = View.INVISIBLE
                ufo.post {
                    ufo.x =
                        (0..(Tool.getScreenWidth() - (ufo.right - ufo.left))).random().toFloat()
                    ufo.y = 100f
                    ufo.tag = ufoIndex
                    ufo.visibility = View.VISIBLE
                    val data = UFOData(ufo, isRight = true, isTop = false)
                    ufoList.add(data)
                    shootUser(ufo)
                    moveUFO(data)
                }
                ufoIndex++
                handler.removeCallbacks(this)
                handler.postDelayed(this, 2000)
            }
        }, 2000)

    }

    /**
     * 移動UFO
     */
    private fun moveUFO(data: UFOData) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (jetHandler.isGameOver || bigBossHandler.bigBossData != null) {
                    handler.removeCallbacks(this)
                    return
                }
                if (data.isRight) {
                    data.ufo.x = data.ufo.x + 10f
                } else {
                    data.ufo.x = data.ufo.x - 10f
                }
                if ((data.ufo.x + (data.ufo.right - data.ufo.left)) >= Tool.getScreenWidth()) {
                    data.isRight = false
                }
                if (data.ufo.x <= 0) {
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
                    data.ufo.y = data.ufo.y + 10f
                } else {
                    data.ufo.y = data.ufo.y - 10f
                }
                if ((data.ufo.y + (data.ufo.bottom - data.ufo.top)) >= Tool.getScreenHeight() / 4) {
                    data.isTop = true
                }
                if (data.ufo.y <= 0) {
                    data.isTop = false
                }
                handler.postDelayed(this, 1)
            }
        }, 1)

    }

    /**
     * 開始射擊 USER
     */
    private fun shootUser(ufo: View) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (jetHandler.isGameOver || bigBossHandler.bigBossData != null) {
                    handler.removeCallbacks(this)
                    return
                }
                if (isUFODestroy(ufo)) {
                    handler.removeCallbacks(this)
                    return
                }
                val bullet = getContext().getUFoBullet()
                root.addView(bullet)
                bullet.visibility = View.INVISIBLE
                bullet.post {
                    bullet.x =
                        (ufo.x + ((ufo.right - ufo.left) / 2)) - ((bullet.right - bullet.left) / 2)
                    bullet.y = ufo.y + (ufo.bottom - ufo.top)
                    bullet.visibility = View.VISIBLE
                    val data = UfoBulletData(bullet)
                    ufoBulletList.add(data)

                    moveUFOBullet(data)
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1000)
            }

        }, 1000)
    }

    /**
     * 移動UFO子彈
     */
    private fun moveUFOBullet(bullet: UfoBulletData) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                bullet.bulletView.y = bullet.bulletView.y + 10f
                if (bullet.bulletView.y >= Tool.getScreenHeight()) {
                    root.removeView(bullet.bulletView)
                    handler.removeCallbacks(this)
                    return
                }
                if (isHitUser(bullet.bulletView)) {
                    showGameOver()
                    MusicTool.stopBgMusic()
                    MusicTool.playGameOverMusic()
                    jetHandler.isGameOver = true
                    root.removeView(bullet.bulletView)
                    ufoBulletList.remove(bullet)
                    handler.removeCallbacks(this)
                    return
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1)
            }
        }, 1)
    }

    /**
     * 遊戲結束顯示Dialog
     */
    private fun showGameOver() {
        onShowGameOverListener.onShowGameOver()
    }

    /**
     * 是否打中USER
     */
    private fun isHitUser(bullet: View): Boolean =
        bullet.x >= jetHandler.jetX &&
                bullet.x <= (jetHandler.jetX + (jetHandler.jetRight - jetHandler.jetLeft)) &&
                bullet.y >= jetHandler.jetY + 20f &&
                bullet.y <= (jetHandler.jetY + (jetHandler.jetBottom - jetHandler.jetTop))

    /**
     * 是否UFO已經被擊敗
     */
    private fun isUFODestroy(ufo: View): Boolean {
        var isDestroy = true
        ufoList.forEach {
            if (it.ufo.tag == ufo.tag) {
                isDestroy = false
            }
        }
        return isDestroy
    }

    /**
     * 清除所有升級箱子 但是這個不應該在這
     */
    private fun clearUpgradeItem() {

    }

    /**
     * 清除所有UFO
     */
    private fun clearAllUFO(root: ConstraintLayout) {
        val ufoIterator = ufoList.iterator()
        while (ufoIterator.hasNext()) {
            val data = ufoIterator.next()
            root.removeView(data.ufo)
            ufoIterator.remove()
        }
    }


    fun interface OnShowGameOverListener{
        fun onShowGameOver()
    }

}