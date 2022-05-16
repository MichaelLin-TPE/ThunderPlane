package com.thunder.thunderplane.playground

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.thunder.thunderplane.R
import com.thunder.thunderplane.background.BackgroundHandler
import com.thunder.thunderplane.base.BaseActivity
import com.thunder.thunderplane.big_boss.BigBossHandler
import com.thunder.thunderplane.databinding.ActivityMainBinding
import com.thunder.thunderplane.dialog.GameOverDialog
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.small_boss.SmallBossHandler
import com.thunder.thunderplane.tool.MusicTool
import com.thunder.thunderplane.tool.ViewTool.BULLET_LEVEL_1
import com.thunder.thunderplane.tool.ViewTool.BULLET_LEVEL_5
import com.thunder.thunderplane.ufo.UFOHandler
import com.thunder.thunderplane.user.JetHandler
import org.koin.android.ext.android.inject


class PlayGroundActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    private var rawX = 0f
    private var rawY = 0f

    private val viewModel: PlayGroundViewModel by viewModels {
        PlayGroundViewModel.MainViewModelFactory(PlayGroundRepositoryImpl())
    }
    private val handler = Handler(Looper.myLooper()!!)

    //handle ufo
    private val ufoHandler by inject<UFOHandler>()

    //handle small boss
    private val smallBossHandler by inject<SmallBossHandler>()

    //handle big boss
    private val bigBossHandler by inject<BigBossHandler>()

    //handle background
    private val backgroundHandler by inject<BackgroundHandler>()

    //handle jet
    private val jetHandler by inject<JetHandler>()


    /**
     *  先暫時移除控制圈
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataBinding.vm = viewModel
        dataBinding.lifecycleOwner = this

        //統一給HANDLER
        jetHandler.setHandler(handler)
        ufoHandler.setHandler(handler)
        smallBossHandler.setHandler(handler)
        bigBossHandler.setHandler(handler)
        backgroundHandler.setHandler(handler)

        initView()

        dataBinding.root.setOnTouchListener(onTouchListener)

        //移動飛機
        viewModel.moveJetLiveData.observe(this) {
            jetHandler.jetTop = it.top
            jetHandler.jetBottom = it.bottom
            jetHandler.jetRight = it.right
            jetHandler.jetLeft = it.left
            jetHandler.jetX = it.jetX
            jetHandler.jetY = it.jetY

            dataBinding.jet.x = it.jetX
            dataBinding.jet.y = it.jetY
        }

        viewModel.scoreLiveData.observe(this) {
            val scoreContent = "score : $it"
            dataBinding.score.text = scoreContent
        }

        //創造小BOSS
        viewModel.createSmallBossLiveData.observe(this) {
            if (!it || jetHandler.isGameOver) {
                return@observe
            }
            smallBossHandler.createSmallBoss(dataBinding.root)
        }

        viewModel.createBigBossLiveData.observe(this) {
            if (!it) {
                return@observe
            }
            bigBossHandler.createBigBoss(dataBinding.root)

        }
        bigBossHandler.setOnShowGameOverListener{
            showGameOver()
        }
        MusicTool.playBgMusic()

    }

    private fun showGameOver() {
        showGameOverDialog(viewModel.scoreLiveData.value!!,
            object : GameOverDialog.OnGameOverDialogClickListener {
                override fun onCloseGame() {
                    finish()

                }

                override fun onRestartGame() {
                    ufoHandler.clearAllBullet()
                    bigBossHandler.clearAllBossBullet()
                    jetHandler.clearUpgradeItem()
                    smallBossHandler.ufoBossList.clear()
                    jetHandler.isGameOver = false
                    MusicTool.playBgMusic()
                    viewModel.reStartScore()
                    initView()
                }
            })
    }


    private fun initView() {
        //一開始先給飛機的位置
        dataBinding.jet.post {
            jetHandler.jetX = dataBinding.jet.x
            jetHandler.jetY = dataBinding.jet.y
            jetHandler.jetRight = dataBinding.jet.right
            jetHandler.jetLeft = dataBinding.jet.left
            jetHandler.jetTop = dataBinding.jet.top
            jetHandler.jetBottom = dataBinding.jet.bottom
        }

        //背景處理完才會開始
        backgroundHandler.startToMoveBackground(dataBinding.bgRoot, dataBinding.scrollView){
            //一開始飛機的子彈為最小化
            jetHandler.setUFOData(ufoHandler,smallBossHandler,bigBossHandler)
            jetHandler.setJetBulletLevel(BULLET_LEVEL_5)


            //產生UFO
            ufoHandler.appearUfo(dataBinding.root)

            ufoHandler.setOnShowGameOverListener {
                showGameOver()
            }

            //產生子彈
            jetHandler.startShooting(dataBinding.root)
            jetHandler.setOnAddScoreListener {
                viewModel.addScore(it)
            }

            bigBossHandler.startToShootUser()

            //每5000分出現小BOSS
            viewModel.onCreateSmallBoss()
        }



    }


    override fun onDestroy() {
        MusicTool.releaseAllMusic()
        jetHandler.isGameOver = false
        super.onDestroy()
    }


    override fun onPause() {
        super.onPause()
        viewModel.onPause()
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

