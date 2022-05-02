package com.thunder.thunderplane

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.thunder.thunderplane.base.MyApplication
import com.thunder.thunderplane.bean.BulletData
import com.thunder.thunderplane.bean.UfoBigBossData
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.MusicTool
import com.thunder.thunderplane.tool.Tool
import com.thunder.thunderplane.tool.ViewTool.getBigBoss

class BigBossHandler(private val jetHandler: JetHandler) {

    var bigBossData : UfoBigBossData? = null
    private lateinit var handler: Handler
    private var bossBulletIndex = 0
    private lateinit var root: ConstraintLayout
    private val bossBulletList = ArrayList<BulletData>()
    var rightWing:ImageView? = null
    var leftWing : ImageView? = null
    var centerObject : ImageView? = null

    private fun getContext(): Context {
        return MyApplication.instance.applicationContext
    }
    private lateinit var onShowGameOverListener: UFOHandler.OnShowGameOverListener


    fun setOnShowGameOverListener(showGameOverListener: UFOHandler.OnShowGameOverListener) {
        onShowGameOverListener = showGameOverListener
    }

    /**
     * 建立大BOSS
     */
    fun createBigBoss(root : ConstraintLayout) {
        this.root = root
        val view = getContext().getBigBoss()
        view.visibility = View.INVISIBLE
        rightWing = view.findViewById(R.id.right_wing)
        leftWing = view.findViewById(R.id.left_wing)
        centerObject = view.findViewById(R.id.center_object)
        root.addView(view)
        view.post {
            view.x = ((Tool.getScreenWidth() - (view.right - view.left)) / 2).toFloat()
            view.y = 0f - (view.bottom - view.top)
            MichaelLog.i("已顯示大BOSS : x : ${view.x} y : ${view.y}")
            view.visibility = View.VISIBLE
            bigBossData = UfoBigBossData(view)
            startToMoveBigBoss(bigBossData!!)
            startToShootUser()
        }
    }

    /**
     * 開始射擊使用這
     */
    fun startToShootUser() {

        if (bigBossData == null){
            return
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (jetHandler.isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                for (i in 0 until 5) {

                    val view =
                        View.inflate(getContext(), R.layout.bullet_layout, null)
                    view.tag = bossBulletIndex
                    root.addView(view)
                    view.visibility = View.INVISIBLE
                    view.post {
                        val xList = mutableListOf<Float>()
                        for (times in 1..5) {
                            val x =
                                (((bigBossData!!.boss.right - bigBossData!!.boss.left) / 4) * times).toFloat()
                            xList.add(x)
                        }
                        val centerX = xList[(0 until xList.size).random()]
                        view.x = centerX
                        view.y =
                            bigBossData!!.boss.y + (bigBossData!!.boss.bottom - bigBossData!!.boss.top)
                        view.visibility = View.VISIBLE
                        bossBulletList.add(BulletData(view, view.x, view.y, bossBulletIndex))
                        moveBossPowerfulBulletY(view)
                        when (i) {
                            0 -> {
                                moveBossPowerfulBulletX(view, false, 15)
                            }
                            1 -> {
                                moveBossPowerfulBulletX(view, false, 35)
                            }
                            2 -> {

                            }
                            3 -> {
                                moveBossPowerfulBulletX(view, true, 35)
                            }
                            else -> {
                                moveBossPowerfulBulletX(view, true, 15)
                            }
                        }
                    }
                    bossBulletIndex++
                }

                handler.postDelayed(this, 3000)
            }
        }, 3000)
    }

    /**
     * 移動BOSS子彈X
     */
    private fun moveBossPowerfulBulletX(view: View, isPlus: Boolean, speed: Int) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlus) {
                    view.x = view.x + 8.0.toFloat()
                    if (view.x >= Tool.getScreenWidth()) {
                        root.removeView(view)
                        handler.removeCallbacks(this)
                        return
                    }
                    updateBossBulletData(view)
                    if (isHitUser(view)) {
                        onShowGameOverListener.onShowGameOver()
                        MusicTool.stopBgMusic()
                        MusicTool.playGameOverMusic()
                        jetHandler.isGameOver = true
                        root.removeView(view)
                        handler.removeCallbacks(this)
                        return
                    }
                    handler.postDelayed(this, speed.toLong())
                    return
                }
                view.x = view.x - 8.0.toFloat()
                if (view.x <= 0) {
                    root.removeView(view)
                    handler.removeCallbacks(this)
                    return
                }
                updateBossBulletData(view)
                if (isHitUser(view)) {
                    onShowGameOverListener.onShowGameOver()
                    MusicTool.stopBgMusic()
                    MusicTool.playGameOverMusic()
                    jetHandler.isGameOver = true
                    root.removeView(view)
                    handler.removeCallbacks(this)
                    return
                }
                handler.postDelayed(this, speed.toLong())
            }
        }, speed.toLong())
    }

    /**
     * 是否命中USER
     */
    private fun isHitUser(bullet: View): Boolean  =
        bullet.x >= jetHandler.jetX &&
    bullet.x <= (jetHandler.jetX + (jetHandler.jetRight - jetHandler.jetLeft)) &&
    bullet.y >= jetHandler.jetY + 20f &&
    bullet.y <= (jetHandler.jetY + (jetHandler.jetBottom - jetHandler.jetTop))

    /**
     * 更新BOSS子彈資料
     */
    private fun updateBossBulletData(view: View) {
        bossBulletList.forEach {
            if (it.bulletView.tag == view.tag) {
                it.y = view.y
            }
        }
    }

    /**
     * 移動BOSS子彈Y
     */
    private fun moveBossPowerfulBulletY(view: View) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                view.y = view.y + 15f
                if (view.y >= Tool.getScreenHeight()) {
                    root.removeView(view)
                    handler.removeCallbacks(this)
                    return
                }
                handler.postDelayed(this, 1)
            }
        }, 1)
    }

    /**
     * 移動大BOSS Y
     */
    private fun startToMoveBigBoss(bigBossData: UfoBigBossData) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (bigBossData.boss.y >= 50f) {
                    handler.removeCallbacks(this)
                    return
                }
                bigBossData.boss.y = bigBossData.boss.y + 1f

                handler.postDelayed(this, 1)
            }

        }, 1)
    }

    fun clearAllBossBullet() {
        bossBulletList.forEach {
            root.removeView(it.bulletView)
        }
        bossBulletList.clear()
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }

}