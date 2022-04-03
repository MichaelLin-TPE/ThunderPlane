package com.thunder.thunderplane

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.thunder.thunderplane.databinding.ActivityMainBinding
import com.thunder.thunderplane.log.MichaelLog

class MainActivity : AppCompatActivity() {

    private lateinit var dataBinding: ActivityMainBinding
    private lateinit var controlView: View

    private var x = 0f
    private var y = 0f
    private var rawX = 0f
    private var rawY = 0f

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        dataBinding.vm = viewModel
        dataBinding.lifecycleOwner = this

        dataBinding.root.setOnTouchListener(onTouchListener)

        //設定圈圈
        viewModel.addControlCircleLiveData.observe(this) {

            if (!it.isShow) {
                dataBinding.circleTarget.visibility = View.INVISIBLE
                dataBinding.root.removeView(controlView)
                return@observe
            }
            controlView = View.inflate(this, R.layout.control_layout, null)
            dataBinding.root.addView(controlView)
            controlView.visibility = View.INVISIBLE
            controlView.post {
                val radius = (controlView.bottom - controlView.top) / 2
                controlView.x = it.x - radius
                controlView.y = it.y - radius
                controlView.visibility = View.VISIBLE
                controlView.post {
                    viewModel.setControlViewWidthHeight(controlView.x,controlView.y,controlView.x + controlView.width,controlView.y + controlView.height)
                }
            }


            val targetRadius = (dataBinding.circleTarget.bottom - dataBinding.circleTarget.top) / 2
            dataBinding.circleTarget.x = it.x - targetRadius
            dataBinding.circleTarget.y = it.y - targetRadius
            dataBinding.circleTarget.visibility = View.VISIBLE

            dataBinding.circleTarget.post {
                x = dataBinding.circleTarget.x - rawX
                y = dataBinding.circleTarget.y - rawY
                viewModel.setTarget(x, y)
            }
        }

        //移動飛機
        viewModel.moveJetLiveData.observe(this) {
            dataBinding.jet.animate().x(it.jetX).y(it.jetY).setDuration(0).start()
        }

        //移動目標圈圈
        viewModel.moveTargetLiveData.observe(this) {
            dataBinding.circleTarget.animate().x(it.targetX).y(it.targetY).setDuration(0).start()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                viewModel.onAddControlCircleListener(true, event.x, event.y)

                rawX = event.rawX
                rawY = event.rawY
                viewModel.setJetXY(
                    dataBinding.jet.x - event.rawX,
                    dataBinding.jet.y - event.rawY
                )
            }
            MotionEvent.ACTION_MOVE -> {
                viewModel.onMoveTargetListener(
                    event.rawX,
                    event.rawY,
                    dataBinding.circleTarget.width,
                    dataBinding.circleTarget.height
                )
                viewModel.onMoveJefListener(
                    event.rawX,
                    event.rawY,
                    dataBinding.jet.width,
                    dataBinding.jet.height
                )

            }
            MotionEvent.ACTION_UP -> {
                viewModel.onAddControlCircleListener(false, 0f, 0f)
            }
        }
        true
    }




    override fun onPause() {
        super.onPause()
        viewModel.onAddControlCircleListener(false, 0f, 0f)
        viewModel.addControlCircleLiveData.removeObservers(this)
    }
}