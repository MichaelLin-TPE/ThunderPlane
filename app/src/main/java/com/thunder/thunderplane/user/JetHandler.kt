package com.thunder.thunderplane.user

import android.content.Context
import android.os.Handler

import android.view.View
import android.view.animation.AlphaAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import com.thunder.thunderplane.R
import com.thunder.thunderplane.small_boss.SmallBossHandler
import com.thunder.thunderplane.ufo.UFOHandler
import com.thunder.thunderplane.base.MyApplication
import com.thunder.thunderplane.bean.BulletData
import com.thunder.thunderplane.bean.UpgradeItemData
import com.thunder.thunderplane.big_boss.BigBossHandler
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.MusicTool
import com.thunder.thunderplane.tool.Tool
import com.thunder.thunderplane.tool.ViewTool
import com.thunder.thunderplane.tool.ViewTool.getExplodeView
import com.thunder.thunderplane.tool.ViewTool.getJetBullet
import com.thunder.thunderplane.tool.ViewTool.getSmallExplodeView
import com.thunder.thunderplane.tool.ViewTool.getUpgradeItem
import kotlinx.coroutines.*
import java.nio.channels.FileLock
import kotlin.coroutines.CoroutineContext

class JetHandler {

    var isGameOver = false

    var jetX: Float = 0f
    var jetY: Float = 0f
    var jetRight: Int = 0
    var jetLeft: Int = 0
    var jetTop: Int = 0
    var jetBottom: Int = 0
    private val bulletList = ArrayList<BulletData>()
    private var bulletIndex = 0
    private lateinit var handler: Handler
    private val upgradeItemList = ArrayList<UpgradeItemData>()
    private var level = 0
    private lateinit var root: ConstraintLayout
    private lateinit var onAddScoreListener: OnAddScoreListener
    private lateinit var ufoHandler: UFOHandler
    private lateinit var smallBossHandler: SmallBossHandler
    private lateinit var bigBossHandler: BigBossHandler


    fun setUFOData(
        ufoHandler: UFOHandler,
        smallBossHandler: SmallBossHandler,
        bigBossHandler: BigBossHandler
    ) {
        this.ufoHandler = ufoHandler
        this.smallBossHandler = smallBossHandler
        this.bigBossHandler = bigBossHandler
    }

    fun setOnAddScoreListener(onAddScoreListener: OnAddScoreListener) {
        this.onAddScoreListener = onAddScoreListener
    }

    private fun getContext(): Context {
        return MyApplication.instance.applicationContext
    }

    /**
     * 清除升級箱子
     */
    fun clearUpgradeItem() {
        upgradeItemList.forEach {
            root.removeView(it.updateItem)
        }
        upgradeItemList.clear()
    }


    /**
     * 開始射擊
     */
    fun startShooting(root: ConstraintLayout) {
        this.root = root
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                if (level == ViewTool.BULLET_LEVEL_5) {
                    createPowerfulBullet(this)
                    return
                }

                val view = getContext().getJetBullet(level)
                view.tag = bulletIndex
                view.visibility = View.INVISIBLE
                root.addView(view)
                view.post {
                    val centerX =
                        (jetX + ((jetRight - jetLeft) / 2)) - ((view.right - view.left) / 2)
                    view.x = centerX
                    view.y = jetY
                    view.visibility = View.VISIBLE
                    //將每個子彈的資料賽進去子彈清單
                    bulletList.add(BulletData(view, view.x, view.y, bulletIndex))
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            view.y = view.y - 15f
                            if (view.y + (view.bottom - view.top) < 0) {
                                deleteBullet(view)
                                handler.removeCallbacks(this)
                                return
                            }

                            updateBulletData(view)
                            if (isCheckBulletHitUFO(view) || isCheckBulletHitBoss(view) || isCheckBulletHitBigBoss(
                                    view
                                )
                            ) {
                                handler.removeCallbacks(this)
                                return
                            }
                            handler.postDelayed(this, 1)
                        }
                    }, 1)
                    handler.removeCallbacks(this)
                    handler.postDelayed(this, 500)
                }
                playGunSound()

                bulletIndex++
            }
        }, 500)
    }

    //建立最強子彈
    private fun createPowerfulBullet(param: Runnable) {
        for (i in 0 until 5) {

            val view =
                View.inflate(getContext(), R.layout.bullet_layout, null)
            view.tag = bulletIndex
            root.addView(view)
            view.visibility = View.INVISIBLE
            view.post {

                val centerX =
                    (jetX + ((jetRight - jetLeft) / 2)) - ((view.right - view.left) / 2)
                view.x = centerX
                view.y = jetY
                view.visibility = View.VISIBLE
                bulletList.add(BulletData(view, view.x, view.y, bulletIndex))
                movePowerfulBulletY(view)
                when (i) {
                    0 -> {
                        movePowerfulBulletX(view, false, 15)
                    }
                    1 -> {
                        movePowerfulBulletX(view, false, 35)
                    }
                    2 -> {
                    }
                    3 -> {
                        movePowerfulBulletX(view, true, 35)
                    }
                    else -> {
                        movePowerfulBulletX(view, true, 15)
                    }
                }
            }
            bulletIndex++
        }
        playGunSound()
        handler.postDelayed(param, 500)

    }

    //移動目前最強子彈的X
    private fun movePowerfulBulletX(view: View, isPlus: Boolean, speed: Int) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isPlus) {
                    view.x = view.x + 8.0.toFloat()
                    if (view.x >= Tool.getScreenWidth()) {
                        root.removeView(view)
                        handler.removeCallbacks(this)
                        return
                    }
                    updateBulletData(view)
//                    if (isCheckBulletHitUFO(view) || isCheckBulletHitBoss(view) || isCheckBulletHitBigBoss(
//                            view
//                        )
//                    ) {
//                        handler.removeCallbacks(this)
//                        return
//                    }
                    handler.postDelayed(this, speed.toLong())
                    return
                }
                view.x = view.x - 8.0.toFloat()
                if (view.x <= 0) {
                    root.removeView(view)
                    handler.removeCallbacks(this)
                    return
                }
                updateBulletData(view)
//                if (isCheckBulletHitUFO(view) || isCheckBulletHitBoss(view) || isCheckBulletHitBigBoss(
//                        view
//                    )
//                ) {
//                    handler.removeCallbacks(this)
//                    return
//                }
                handler.postDelayed(this, speed.toLong())
            }
        }, speed.toLong())
    }

    /**
     * 檢查是否命中BIG BOSS
     */
    private fun isCheckBulletHitBigBoss(view: View): Boolean {
        if (bigBossHandler.bigBossData == null) {
            return false
        }

        if (isHitLeftWing(view.y, view.x) || isHitRightWing(view.y, view.x) || isHitBody(
                view.y,
                view.x
            )
        ) {
            if (bigBossHandler.bigBossData!!.hp > 0) {
                MichaelLog.i("boss hp : ${bigBossHandler.bigBossData!!.hp}")

                bigBossHandler.bigBossData!!.hp =
                    bigBossHandler.bigBossData!!.hp - ViewTool.getDamage(level)

                val explode = getContext().getSmallExplodeView()
                root.addView(explode)
                explode.visibility = View.INVISIBLE
                explode.post {
                    val explodeWidth = explode.right - explode.left
                    val explodeHeight = explode.bottom - explode.top
                    val bulletWidth = view.right - view.left
                    explode.x = view.x - ((explodeWidth - bulletWidth) / 2)
                    explode.y = view.y - explodeHeight
                    explode.visibility = View.VISIBLE
                    handler.postDelayed({
                        root.removeView(explode)
                    },1000)
                    deleteBullet(view)
                }

//                deleteBullet(view)
//                val alphaAnimation = AlphaAnimation(1.0f, 0.5f)
//                alphaAnimation.duration = 100
//                alphaAnimation.fillAfter = false
//                bigBossHandler.bigBossData!!.boss.startAnimation(alphaAnimation)
                return true
            }
            createRandomUpgradeItem(view.x, view.y)
            createExplodeView(view.x, view.y)
            root.removeView(bigBossHandler.bigBossData!!.boss)
            deleteBullet(view)
            onAddScoreListener.onAddScore(1000)
            return true
        }
        return false
    }

    private fun isHitBody(y: Float, x: Float): Boolean {
        if (bigBossHandler.bigBossData?.body == null) {
            return false
        }
        val bodyLocation = intArrayOf(0, 0)
        bigBossHandler.bigBossData!!.body.getLocationOnScreen(bodyLocation)

        val bodyX = bodyLocation[0]
        val bodyY = bodyLocation[1]

        if (y <= (bodyY + (bigBossHandler.bigBossData!!.body.bottom - bigBossHandler.bigBossData!!.body.top)) &&
            x >= bodyX && x <= (bodyX + (bigBossHandler.bigBossData!!.body.right - bigBossHandler.bigBossData!!.body.left))
        ) {
            return true
        }
        return false
    }

    private fun isHitRightWing(y: Float, x: Float): Boolean {
        if (bigBossHandler.bigBossData?.rightWing == null) {
            return false
        }
        val rightWingLocation = intArrayOf(0, 0)
        bigBossHandler.bigBossData!!.rightWing.getLocationOnScreen(rightWingLocation)

        val rightWingX = rightWingLocation[0]
        val rightWingY = rightWingLocation[1]
        if (y <= (rightWingY + (bigBossHandler.rightWing!!.bottom - bigBossHandler.rightWing!!.top)) &&
            x >= rightWingX && x <= (rightWingX + (bigBossHandler.rightWing!!.right - bigBossHandler.rightWing!!.left))
        ) {
            return true
        }
        return false
    }

    private fun isHitLeftWing(y: Float, x: Float): Boolean {
        if (bigBossHandler.bigBossData?.leftWing == null) {
            return false
        }
        val leftWingLocation = intArrayOf(0, 0)
        bigBossHandler.bigBossData!!.leftWing.getLocationOnScreen(leftWingLocation)

        val leftWingX = leftWingLocation[0]
        val rightWingY = leftWingLocation[1]
        if (y <= (rightWingY + (bigBossHandler.leftWing!!.bottom - bigBossHandler.leftWing!!.top)) &&
            x >= leftWingX && x <= (leftWingX + (bigBossHandler.leftWing!!.right - bigBossHandler.leftWing!!.left))
        ) {
            return true
        }
        return false
    }

    /**
     * 檢查是否命中BOSS
     */
    private fun isCheckBulletHitBoss(view: View): Boolean {
        if (smallBossHandler.ufoBossList.isEmpty()) {
            return false
        }
        val iterator = smallBossHandler.ufoBossList.iterator()
        while (iterator.hasNext()) {
            val ufoBossData = iterator.next()
            if (view.y >= ufoBossData.boss.y &&
                view.y <= (ufoBossData.boss.y + (ufoBossData.boss.bottom - ufoBossData.boss.top)) &&
                view.x >= ufoBossData.boss.x &&
                view.x <= (ufoBossData.boss.x + (ufoBossData.boss.right - ufoBossData.boss.left))
            ) {
                if (ufoBossData.hp > 0) {
                    MichaelLog.i("boss hp : ${ufoBossData.hp}")
                    deleteBullet(view)
                    ufoBossData.hp = ufoBossData.hp - ViewTool.getDamage(level)
                    val alphaAnimation = AlphaAnimation(1.0f, 0.2f)
                    alphaAnimation.duration = 100
                    alphaAnimation.fillAfter = false
                    ufoBossData.boss.startAnimation(alphaAnimation)
                    return true
                }
                createRandomUpgradeItem(view.x, view.y)
                createExplodeView(view.x, view.y)
                root.removeView(ufoBossData.boss)
                deleteBullet(view)
                iterator.remove()
                onAddScoreListener.onAddScore(500)
                return true
            }
        }
        return false
    }

    /**
     * 機率性產生升級
     */
    private fun createRandomUpgradeItem(x: Float, y: Float) {
        if (!Tool.isCreateUpgradeItem()) {
            return
        }
        val view = getContext().getUpgradeItem()
        root.addView(view)
        view.visibility = View.INVISIBLE
        view.post {
            view.x = x
            view.y = y
            view.visibility = View.VISIBLE
            val data = UpgradeItemData(view, isRight = true, isTop = false)
            upgradeItemList.add(data)
            moveUpgradeItem(data)
        }
    }

    /**
     * 移動升級箱子
     */
    private fun moveUpgradeItem(data: UpgradeItemData) {

        handler.postDelayed(object : Runnable {
            override fun run() {

                if (data.updateItem.x > jetX &&
                    data.updateItem.x <= (jetX + (jetRight - jetLeft)) &&
                    data.updateItem.y > jetY &&
                    data.updateItem.y <= (jetY + (jetBottom - jetTop))
                ) {
                    MusicTool.playUpgradeMusic()
                    root.removeView(data.updateItem)
                    upgradeItemList.remove(data)
                    level = ViewTool.upgradeBulletLevel(level)
                    handler.removeCallbacks(this)
                    return
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1)
            }
        }, 1)


        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                if (data.isRight) {
                    data.updateItem.x = data.updateItem.x + 1f
                } else {
                    data.updateItem.x = data.updateItem.x - 1f
                }
                if ((data.updateItem.x + (data.updateItem.right - data.updateItem.left)) >= Tool.getScreenWidth()) {
                    data.isRight = false
                }
                if (data.updateItem.x <= 0) {
                    data.isRight = true
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1)
            }
        }, 1)

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                if (!data.isTop) {
                    data.updateItem.y = data.updateItem.y + 10f
                } else {
                    data.updateItem.y = data.updateItem.y - 10f
                }
                if ((data.updateItem.y + (data.updateItem.bottom - data.updateItem.top)) >= Tool.getScreenHeight()) {
                    data.isTop = true
                }
                if (data.updateItem.y <= 0) {
                    data.isTop = false
                }
                handler.postDelayed(this, 1)
            }
        }, 1)
    }

    private fun isCheckBulletHitUFO(view: View): Boolean {
        val ufoIterator = ufoHandler.ufoList.iterator()
        while (ufoIterator.hasNext()) {
            val ufoData = ufoIterator.next()
            if (view.y >= ufoData.ufo.y &&
                view.y <= (ufoData.ufo.y + (ufoData.ufo.bottom - ufoData.ufo.top)) &&
                view.x >= ufoData.ufo.x &&
                view.x <= (ufoData.ufo.x + (ufoData.ufo.right - ufoData.ufo.left))
            ) {
//                createRandomUpgradeItem(view.x, view.y)
                createExplodeView(view.x, view.y)
                root.removeView(ufoData.ufo)
                deleteBullet(view)
                ufoIterator.remove()
                onAddScoreListener.onAddScore(100)
                return true
            }
        }
        return false
    }

    private fun createExplodeView(x: Float, y: Float) {

        val bgScope: CoroutineScope = object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = Dispatchers.IO
        }
        val mainScope: CoroutineScope = object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = Dispatchers.Main
        }

        val view = getContext().getExplodeView()

        root.addView(view)
        view.visibility = View.INVISIBLE
        view.post {
            view.x = x
            view.y = y
            view.visibility = View.VISIBLE
            bgScope.launch {
                delay(1000)
                mainScope.launch {
                    root.removeView(view)
                }
            }
        }
    }

    //更新子彈數據
    private fun updateBulletData(view: View) {
        bulletList.forEach {
            if (it.bulletView.tag == view.tag) {
                it.y = view.y
            }
        }

    }

    //移動目前最強子彈的Y
    private fun movePowerfulBulletY(view: View) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                view.y = view.y - 15f
                if (view.y <= 0) {
                    root.removeView(view)
                    handler.removeCallbacks(this)
                    return
                }
                if (isCheckBulletHitUFO(view) || isCheckBulletHitBoss(view) || isCheckBulletHitBigBoss(
                        view
                    )
                ) {
                    handler.removeCallbacks(this)
                    return
                }
                handler.postDelayed(this, 1)
            }
        }, 1)
    }

    //刪除非必要子彈
    private fun deleteBullet(view: View) {
        root.removeView(view)
        val iterator = bulletList.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            if (data.bulletView.tag == view.tag) {
                iterator.remove()

            }
        }
    }

    private fun playGunSound() {
        MusicTool.playShootMusic()
    }

    fun setJetBulletLevel(level: Int) {
        this.level = level
    }

    fun setHandler(handler: Handler) {
        this.handler = handler
    }

    fun interface OnAddScoreListener {
        fun onAddScore(score: Int)
    }

}