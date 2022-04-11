package com.thunder.thunderplane

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.thunder.thunderplane.bean.BulletData
import com.thunder.thunderplane.bean.UFOData
import com.thunder.thunderplane.databinding.ActivityMainBinding
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.UITool
import com.thunder.thunderplane.tool.UITool.getPixel
import com.thunder.thunderplane.tool.UITool.getRandomBackground
import com.thunder.thunderplane.wedgit.RandomBgView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    private var rawX = 0f
    private var rawY = 0f
    private val handler = Handler(Looper.myLooper()!!)
    private val bulletList = ArrayList<BulletData>()
    private val ufoList = ArrayList<UFOData>()
    private var bulletIndex = 0
    private var ufoIndex = 0
    private var mPlayer: MediaPlayer? = null

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(MainRepositoryImpl())
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
        mPlayer = MediaPlayer.create(this, R.raw.gun_shot)
        initView()


        //產生UFO
        appearUFO()
        //產生子彈
        startShooting()

        MichaelLog.i("開始")
        dataBinding.root.setOnTouchListener(onTouchListener)


        //移動飛機
        viewModel.moveJetLiveData.observe(this) {
            dataBinding.jet.x = it.jetX
            dataBinding.jet.y = it.jetY
        }

    }

    private fun initView() {

        lifecycleScope.launch(Dispatchers.IO){
            val bgList = ArrayList<BgData>()
            for( i in 0..200){
                val view = RandomBgView(this@MainActivity)
                lifecycleScope.launch {
                    dataBinding.bgRoot.addView(view)
                }
                view.post{
                    viewSetting(view)
                    val data = BgData(view,view.x,view.y)
                    bgList.add(data)
                    MichaelLog.i("x : ${view.x} , y : ${view.y}")
                    if (bgList.size == 201){
                        dataBinding.scrollView.scrollTo(0,bgList[bgList.size - 1].y.toInt())
                        MichaelLog.i("viewCount : ${dataBinding.bgRoot.childCount}")
                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                dataBinding.bgRoot.y = dataBinding.bgRoot.y + 1f

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
        layoutParams.height = UITool.getScreenHeight()
        layoutParams.width = UITool.getScreenWidth()
        view.layoutParams = layoutParams
    }


    private fun appearUFO() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val ufo = View.inflate(this@MainActivity, R.layout.ufo_layout, null)
                dataBinding.root.addView(ufo)
                ufo.visibility = View.INVISIBLE
                ufo.post {
                    ufo.x =
                        (0..(UITool.getScreenWidth() - (ufo.right - ufo.left))).random().toFloat()
                    ufo.y = 100f
                    ufo.tag = ufoIndex
                    ufo.visibility = View.VISIBLE
                    val data = UFOData(ufo, isRight = true, isTop = false)
                    ufoList.add(data)
                    moveUFO(data)
                }
                ufoIndex++
                handler.removeCallbacks(this)
                handler.postDelayed(this, 1000)
            }
        }, 1000)

    }

    private fun moveUFO(data: UFOData) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (data.isRight) {
                    data.ufo.x = data.ufo.x + 10f
                } else {
                    data.ufo.x = data.ufo.x - 10f
                }
                if ((data.ufo.x + (data.ufo.right - data.ufo.left)) >= UITool.getScreenWidth()) {
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
                if (!data.isTop) {
                    data.ufo.y = data.ufo.y + 10f
                } else {
                    data.ufo.y = data.ufo.y - 10f
                }
                if ((data.ufo.y + (data.ufo.bottom - data.ufo.top)) >= UITool.getScreenHeight() / 4) {
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
                val view = View.inflate(this@MainActivity, R.layout.bullet_layout, null)
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
                            view.animate().y(view.y - 100f).setDuration(0).start()
                            if (view.y + (view.bottom - view.top) < 0) {
                                deleteBullet(view)
                                handler.removeCallbacks(this)
                                return
                            }
                            updateBulletData(view)
                            checkBulletHitUFO(view)
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

    private fun playGunSound() {
        mPlayer?.start()
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
                createExplodeView(view.x,view.y)
                dataBinding.root.removeView(ufoData.ufo)
                deleteBullet(view)
                ufoIterator.remove()
            }
        }
    }

    private fun createExplodeView(x: Float, y: Float) {
        val view = View.inflate(this,R.layout.explode_layout,null)

        dataBinding.root.addView(view)
        view.visibility = View.INVISIBLE
        setExplodeSize(view)
        view.post {
            view.x = x
            view.y = y
            Log.i("Michael","x : $x , y : $y")
            view.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                delay(1000)
                lifecycleScope.launch(Dispatchers.Main) {
                    dataBinding.root.removeView(view)
                }
            }
        }
    }

    private fun setExplodeSize(view: View) {
        val layoutParams = view.layoutParams
        layoutParams.width = this.getPixel(60)
        layoutParams.width = this.getPixel(60)
        view.layoutParams = layoutParams
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

