package com.thunder.thunderplane.level_page

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.thunder.thunderplane.R
import com.thunder.thunderplane.base.BaseActivity
import com.thunder.thunderplane.bean.LevelData
import com.thunder.thunderplane.databinding.ActivityLevelBinding
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.playground.PlayGroundActivity
import com.thunder.thunderplane.tool.Tool

class LevelActivity : BaseActivity() {

    private lateinit var dataBinding: ActivityLevelBinding
    private val viewModel: LevelViewModel by viewModels {
        val repository: LevelRepository = LevelRepositoryImpl()
        LevelViewModel.LevelViewModelFactory(repository)
    }

    companion object {
        const val LEVEL_KEY = "level"
        const val LEFT = 0
        const val RIGHT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_level)
        dataBinding.vm = viewModel
        dataBinding.lifecycleOwner = this

        viewModel.onActivityCreate()

        handleLiveData()
    }

    private fun handleLiveData() {
        viewModel.levelDataListLiveData.observe(this) { dataList ->
            startToAddView(dataList)
        }
    }

    private fun startToAddView(dataList: MutableList<LevelData>) {
        dataBinding.levelAddView.removeAllViews()
        dataList.forEachIndexed { index, data ->
            if (index % 2 == 0) {
                val view = View.inflate(this@LevelActivity, R.layout.left_planet_layout, null)
                view.tag = data.tag
                showData(data, view)
                dataBinding.levelAddView.addView(view)
                createRandomStar(view, LEFT )
            } else {
                val view = View.inflate(this@LevelActivity, R.layout.right_planet_layout, null)
                view.tag = data.tag
                showData(data, view)
                dataBinding.levelAddView.addView(view)
                createRandomStar(view, RIGHT)
                if (index == dataList.size - 1) {
                    view.post {
                        dataBinding.levelScrollView.smoothScrollTo(0, view.y.toInt())
                    }
                }
            }
        }


    }

    private fun createRandomStar(view: View, type: Int) {

        view.post {
            MichaelLog.i("start view y : ${view.y}")
            val planet: ImageView = view.findViewById(R.id.galaxy_icon)
            val root: ConstraintLayout = view.findViewById(R.id.root)
            for (i in 0..3) {
                val starView = View.inflate(this, R.layout.star_layout, null)
                starView.visibility = View.INVISIBLE
                root.addView(starView)
                starView.post {
                    val y: Int = 0
                    val bottomY: Int = view.bottom - view.top
                    val randomY = (y until bottomY).random()
                    MichaelLog.i("randomY : $randomY")
                    val x: Int = if (type == LEFT) (planet.x + (planet.right - planet.left)).toInt() else planet.x.toInt()
                    val endX = if (type == LEFT) Tool.getScreenWidth() else 0
                    val randomX = if (type == LEFT) (x until endX).random() else (endX until x).random()
                    starView.y = randomY.toFloat()
                    starView.x = randomX.toFloat()
                    starView.visibility = View.VISIBLE
                    startAnimation(starView)
                }
            }
        }
    }

    private fun startAnimation(view: View) {
        val animation = ScaleAnimation(0.95f,1f,0.95f,1f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        animation.duration = 900
        animation.repeatCount = -1
        animation.repeatMode = Animation.REVERSE
        view.startAnimation(animation)
    }

    private fun showData(data: LevelData, view: View) {
        val tvTitle: TextView = view.findViewById(R.id.galaxy_title)
        tvTitle.text = data.title
        val ivIcon: ImageView = view.findViewById(R.id.galaxy_icon)
        ivIcon.setImageResource(data.imageId)
        Tool.startRotate(ivIcon, 360f)
        view.setOnClickListener {
            val intent = Intent(this@LevelActivity, PlayGroundActivity::class.java)
            intent.putExtra(LEVEL_KEY, view.tag as Int)
            startActivity(intent)
        }
    }
}