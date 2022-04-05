package com.thunder.thunderplane

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.thunder.thunderplane.databinding.ActivityMainBinding
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.UITool

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding

    private var rawX = 0f
    private var rawY = 0f
    private val handler = Handler(Looper.myLooper()!!)
    private var isRotation = false
    private var isMove = false
    private var moveX = 0f
    private var set = AnimationSet(true)

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory()
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
        appearUFO()
        startShooting()

        dataBinding.root.setOnTouchListener(onTouchListener)

        //移動飛機
        viewModel.moveJetLiveData.observe(this) {
            dataBinding.jet.animate().x(it.jetX).y(it.jetY).setDuration(0).start()
        }

    }

    private fun appearUFO() {

        val ufo = View.inflate(this, R.layout.ufo_layout, null)
        dataBinding.root.addView(ufo)
        ufo.post {
            ufo.x = ((UITool.getScreenWidth() / 2) - ((ufo.right - ufo.left) / 2)).toFloat()
            ufo.y = 100f
            letUFORotation(ufo)
            randomMoveUFO(ufo)

        }

    }

    private fun randomMoveUFO(ufo: View) {

        isMove = !isMove
        MichaelLog.i("ufo before x : ${ufo.x} , screen width : ${UITool.getScreenWidth()}")
        val leftX = UITool.getScreenWidth() - ufo.x
        val randomX = (0..UITool.getScreenWidth() - leftX.toInt()).random().toFloat()
        MichaelLog.i("random x : $randomX")
        val animation = TranslateAnimation(
            0f,
            randomX,
            0f,
            0f
        )
        animation.fillAfter = true
        animation.duration = 1500
        ufo.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
//                val loaction = IntArray(2)
//                ufo.getLocationInWindow(loaction)
//                MichaelLog.i("ufo x : ${loaction[0]}")
                ufo.x = 0f
                MichaelLog.i("Ufo x : ${ufo.x}")
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })


    }

    //讓 UFO 轉
    private fun letUFORotation(ufo: View) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                isRotation = !isRotation

                ufo.animate().rotation(if (isRotation) 15f else -45f).setDuration(500).start()

                handler.postDelayed(this, 800)
            }

        }, 800)
    }

    private fun startShooting() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val view = View.inflate(this@MainActivity, R.layout.bullet_layout, null)
                dataBinding.root.addView(view)
                view.post {
                    val centerX =
                        (dataBinding.jet.x + ((dataBinding.jet.right - dataBinding.jet.left) / 2)) - ((view.right - view.left) / 2)
                    view.x = centerX
                    view.y = dataBinding.jet.y
                    handler.postDelayed(object : Runnable {
                        override fun run() {

                            view.animate().y(view.y - 100f).setDuration(0).start()
                            if (view.bottom < 0) {
                                handler.removeCallbacks(this)
                                return
                            }
                            handler.postDelayed(this, 100)
                        }
                    }, 100)
                    handler.postDelayed(this, 500)
                }
            }
        }, 500)
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
                    dataBinding.jet.height
                )
            }
        }
        true
    }
}

