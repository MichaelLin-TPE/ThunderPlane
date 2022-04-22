package com.thunder.thunderplane

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.thunder.thunderplane.base.BaseActivity
import com.thunder.thunderplane.bean.*
import com.thunder.thunderplane.databinding.ActivityMainBinding
import com.thunder.thunderplane.dialog.GameOverDialog
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.MusicTool
import com.thunder.thunderplane.tool.Tool
import com.thunder.thunderplane.tool.ViewTool
import com.thunder.thunderplane.tool.ViewTool.BULLET_LEVEL_1
import com.thunder.thunderplane.tool.ViewTool.getBossBullet
import com.thunder.thunderplane.tool.ViewTool.getExplodeView
import com.thunder.thunderplane.tool.ViewTool.getJetBullet
import com.thunder.thunderplane.tool.ViewTool.getRandomUFOView
import com.thunder.thunderplane.tool.ViewTool.getSmallBoss
import com.thunder.thunderplane.tool.ViewTool.getUFoBullet
import com.thunder.thunderplane.tool.ViewTool.getUpgradeItem
import com.thunder.thunderplane.wedgit.RandomBgView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayGroundActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    private var rawX = 0f
    private var rawY = 0f
    private val handler = Handler(Looper.myLooper()!!)
    private val bulletList = ArrayList<BulletData>()
    private val ufoList = ArrayList<UFOData>()
    private val upgradeItemList = ArrayList<UpgradeItemData>()
    private val ufoBulletList = ArrayList<UfoBulletData>()
    private var bulletIndex = 0
    private var ufoIndex = 0
    private var bossIndex = 0
    private var isGameOver = false
    private val ufoBossList = ArrayList<UfoBossData>()

    private val viewModel: PlayGroundViewModel by viewModels {
        PlayGroundViewModel.MainViewModelFactory(PlayGroundRepositoryImpl())
    }

    /**
     *  先暫時移除控制圈
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataBinding.vm = viewModel
        dataBinding.lifecycleOwner = this
        //一開始飛機的子彈為最小化
        dataBinding.jet.tag = BULLET_LEVEL_1
        initView()

        //產生UFO
        appearUFO()
        //產生子彈
        startShooting()

        MichaelLog.i("開始")
        dataBinding.root.setOnTouchListener(onTouchListener)

        //每5000分出現小BOSS
        viewModel.onCreateSmallBoss()


        //移動飛機
        viewModel.moveJetLiveData.observe(this) {
            dataBinding.jet.x = it.jetX
            dataBinding.jet.y = it.jetY
        }

        viewModel.scoreLiveData.observe(this) {
            dataBinding.score.text = "score : $it"
        }

        //創造小BOSS
        viewModel.createSmallBossLiveData.observe(this) {
            if (!it || isGameOver) {
                return@observe
            }
            createSmallBoss()
        }

        MusicTool.playBgMusic()

    }

    private fun createSmallBoss() {
        val view = this.getSmallBoss()
        dataBinding.root.addView(view)
        view.visibility = View.INVISIBLE
        view.post {
            view.x =
                (0..(Tool.getScreenWidth() - (view.right - view.left))).random().toFloat()
            view.y = 100f
            view.tag = bossIndex
            bossIndex++
            view.visibility = View.VISIBLE
            MichaelLog.i("生成BOSS")
            val ufoBossData = UfoBossData(view, isRight = true, isTop = false)
            ufoBossList.add(ufoBossData)
            moveBoss(ufoBossData)
            bossShootUser(view)
        }
    }

    private fun bossShootUser(view: View) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                if (isUFOBossDestroy(view.tag)) {
                    handler.removeCallbacks(this)
                    return
                }
                val bullet = this@PlayGroundActivity.getBossBullet()
                dataBinding.root.addView(bullet)
                bullet.visibility = View.INVISIBLE
                bullet.post {
                    bullet.x =
                        (view.x + ((view.right - view.left) / 2)) - ((bullet.right - bullet.left) / 2)
                    bullet.y = view.y + (view.bottom - view.top)
                    bullet.visibility = View.VISIBLE
                    val data = UfoBulletData(bullet)
                    ufoBulletList.add(data)

                    moveUFOBullet(data)
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 500)
            }

        }, 500)
    }

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

    private fun moveBoss(data: UfoBossData) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    dataBinding.root.removeView(data.boss)
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
                if (isGameOver) {
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

    private fun initView() {

        lifecycleScope.launch(Dispatchers.IO) {
            val bgList = ArrayList<BgData>()
            for (i in 0..200) {
                val view = RandomBgView(this@PlayGroundActivity)
                lifecycleScope.launch {
                    dataBinding.bgRoot.addView(view)
                }
                view.post {
                    viewSetting(view)
                    view.visibility = View.INVISIBLE
                    val data = BgData(view, view.x, view.y)
                    bgList.add(data)
                    if (bgList.size == 201) {
                        dataBinding.scrollView.scrollTo(0, bgList[bgList.size - 1].y.toInt())
                        bgList.forEach {
                            it.view.visibility = View.VISIBLE
                        }
                        MichaelLog.i("viewCount : ${dataBinding.bgRoot.childCount}")
                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                if (isGameOver) {
                                    handler.removeCallbacks(this)
                                    return
                                }
                                dataBinding.bgRoot.y = dataBinding.bgRoot.y + 5f

                                handler.postDelayed(this, 1)
                            }
                        }, 1)
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


    private fun appearUFO() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    clearAllUFO()
                    clearUpgradeItem()
                    handler.removeCallbacks(this)
                    return
                }
                val ufo = this@PlayGroundActivity.getRandomUFOView()
                dataBinding.root.addView(ufo)
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

    private fun clearUpgradeItem() {
        upgradeItemList.forEach {
            dataBinding.root.removeView(it.updateItem)
        }
        upgradeItemList.clear()
    }

    private fun clearAllUFO() {
        val ufoIterator = ufoList.iterator()
        while (ufoIterator.hasNext()) {
            val data = ufoIterator.next()
            dataBinding.root.removeView(data.ufo)
            ufoIterator.remove()
        }
    }

    private fun shootUser(ufo: View) {

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                if (isUFODestroy(ufo)) {
                    handler.removeCallbacks(this)
                    return
                }
                val bullet = this@PlayGroundActivity.getUFoBullet()
                dataBinding.root.addView(bullet)
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
                handler.postDelayed(this, 500)
            }

        }, 500)


    }

    private fun isUFODestroy(ufo: View): Boolean {
        var isDestroy = true
        ufoList.forEach {
            if (it.ufo.tag == ufo.tag) {
                isDestroy = false
            }
        }
        return isDestroy
    }

    //移動UFO的子彈打向玩家
    private fun moveUFOBullet(bullet: UfoBulletData) {

        handler.postDelayed(object : Runnable {
            override fun run() {
                bullet.bulletView.y = bullet.bulletView.y + 10f
                if (bullet.bulletView.y >= Tool.getScreenHeight()) {
                    dataBinding.root.removeView(bullet.bulletView)
                    handler.removeCallbacks(this)
                    return
                }
                if (isHitUser(bullet.bulletView)) {
                    showGameOver()
                    MusicTool.stopBgMusic()
                    MusicTool.playGameOverMusic()
                    isGameOver = true
                    dataBinding.root.removeView(bullet.bulletView)
                    ufoBulletList.remove(bullet)
                    handler.removeCallbacks(this)
                    return
                }
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1)
            }
        }, 1)
    }

    private fun showGameOver() {
        showGameOverDialog(viewModel.scoreLiveData.value!!,
            object : GameOverDialog.OnGameOverDialogClickListener {
                override fun onCloseGame() {
                    finish()

                }

                override fun onRestartGame() {
                    clearAllBullet()
                    ufoBossList.clear()
                    isGameOver = false
                    MusicTool.playBgMusic()
                    viewModel.reStartScore()
                    initView()
                    appearUFO()
                    startShooting()
                }
            })
    }

    private fun clearAllBullet() {
        ufoBulletList.forEach {
            dataBinding.root.removeView(it.bulletView)
        }
        ufoBulletList.clear()
    }

    private fun isHitUser(bullet: View): Boolean =
        bullet.x >= dataBinding.jet.x &&
                bullet.x <= (dataBinding.jet.x + (dataBinding.jet.right - dataBinding.jet.left)) &&
                bullet.y >= dataBinding.jet.y + 20f &&
                bullet.y <= (dataBinding.jet.y + (dataBinding.jet.bottom - dataBinding.jet.top))


    private fun moveUFO(data: UFOData) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
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
                if (isGameOver) {
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


    private fun startShooting() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (isGameOver) {
                    handler.removeCallbacks(this)
                    return
                }
                val view = this@PlayGroundActivity.getJetBullet(dataBinding.jet.tag)
                view.tag = bulletIndex
                dataBinding.root.addView(view)
                view.post {
                    val centerX =
                        (dataBinding.jet.x + ((dataBinding.jet.right - dataBinding.jet.left) / 2)) - ((view.right - view.left) / 2)
                    view.x = centerX
                    view.y = dataBinding.jet.y
                    //將每個子彈的資料賽進去子彈清單
                    bulletList.add(BulletData(view, view.x, view.y, bulletIndex))
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            view.y = view.y - 100f
                            if (view.y + (view.bottom - view.top) < 0) {
                                deleteBullet(view)
                                handler.removeCallbacks(this)
                                return
                            }

                            updateBulletData(view)
                            checkBulletHitUFO(view)
                            checkBulletHitBoss(view)
                            handler.postDelayed(this, 50)
                        }
                    }, 50)
                    handler.removeCallbacks(this)
                    handler.postDelayed(this, 300)
                }
                playGunSound()

                bulletIndex++
            }
        }, 300)
    }

    override fun onDestroy() {
        super.onDestroy()
        MusicTool.releaseAllMusic()
    }

    /**
     * 檢查是否命中BOSS
     */
    private fun checkBulletHitBoss(view: View) {
        if (ufoBossList.isEmpty()) {
            return
        }
        val iterator = ufoBossList.iterator()
        while (iterator.hasNext()) {
            val ufoBossData = iterator.next()
            if (view.y >= ufoBossData.boss.y &&
                view.y <= (ufoBossData.boss.y + (ufoBossData.boss.bottom - ufoBossData.boss.top)) &&
                view.x >= ufoBossData.boss.x &&
                view.x <= (ufoBossData.boss.x + (ufoBossData.boss.right - ufoBossData.boss.left))
            ) {
                if (ufoBossData.hp > 0) {
                    MichaelLog.i("boss hp : ${ufoBossData.hp}")
                    ufoBossData.hp = ufoBossData.hp - ViewTool.getDamage(dataBinding.jet.tag)
                    val alphaAnimation = AlphaAnimation(1.0f, 0.2f)
                    alphaAnimation.duration = 100
                    alphaAnimation.fillAfter = false
                    ufoBossData.boss.startAnimation(alphaAnimation)
                    continue
                }
                createRandomUpgradeItem(view.x, view.y)
                createExplodeView(view.x, view.y)
                dataBinding.root.removeView(ufoBossData.boss)
                deleteBullet(view)
                iterator.remove()
                viewModel.addScore(500)
            }
        }
    }

    private fun playGunSound() {
        MusicTool.playShootMusic()
    }

    private fun checkBulletHitUFO(view: View) {
        val ufoIterator = ufoList.iterator()
        while (ufoIterator.hasNext()) {
            val ufoData = ufoIterator.next()
            if (view.y >= ufoData.ufo.y &&
                view.y <= (ufoData.ufo.y + (ufoData.ufo.bottom - ufoData.ufo.top)) &&
                view.x >= ufoData.ufo.x &&
                view.x <= (ufoData.ufo.x + (ufoData.ufo.right - ufoData.ufo.left))
            ) {
//                createRandomUpgradeItem(view.x, view.y)
                createExplodeView(view.x, view.y)
                dataBinding.root.removeView(ufoData.ufo)
                deleteBullet(view)
                ufoIterator.remove()
                viewModel.addScore(100)
            }
        }
    }

    /**
     * 機率性產生升級
     */
    private fun createRandomUpgradeItem(x: Float, y: Float) {
        if (!Tool.isCreateUpgradeItem()) {
            return
        }
        val view = this.getUpgradeItem()
        dataBinding.root.addView(view)
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

    private fun moveUpgradeItem(data: UpgradeItemData) {

        handler.postDelayed(object : Runnable {
            override fun run() {

                if (data.updateItem.x > dataBinding.jet.x &&
                    data.updateItem.x <= (dataBinding.jet.x + (dataBinding.jet.right - dataBinding.jet.left)) &&
                    data.updateItem.y > dataBinding.jet.y &&
                    data.updateItem.y <= (dataBinding.jet.y + (dataBinding.jet.bottom - dataBinding.jet.top))
                ) {
                    MusicTool.playUpgradeMusic()
                    dataBinding.root.removeView(data.updateItem)
                    upgradeItemList.remove(data)
                    dataBinding.jet.tag = ViewTool.upgradeBulletLevel(dataBinding.jet.tag)
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

    private fun createExplodeView(x: Float, y: Float) {
        val view = this.getExplodeView()

        dataBinding.root.addView(view)
        view.visibility = View.INVISIBLE
        view.post {
            view.x = x
            view.y = y
            view.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1000)
                lifecycleScope.launch(Dispatchers.Main) {
                    dataBinding.root.removeView(view)
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

    //刪除非必要子彈
    private fun deleteBullet(view: View) {
        dataBinding.root.removeView(view)
        val iterator = bulletList.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            if (data.bulletView.tag == view.tag) {
                iterator.remove()

            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                rawX = event.rawX
                rawY = event.rawY
                viewModel.setJetXY(
                    dataBinding.jet.x - event.rawX,
                    dataBinding.jet.y - event.rawY
                )
            }
            MotionEvent.ACTION_MOVE -> {
                viewModel.onMoveJefListener(
                    event.rawX,
                    event.rawY,
                    dataBinding.jet.width,
                    dataBinding.jet.height,
                    dataBinding.jet.right,
                    dataBinding.jet.left,
                    dataBinding.jet.top,
                    dataBinding.jet.bottom
                )
            }
        }
        true
    }
}

