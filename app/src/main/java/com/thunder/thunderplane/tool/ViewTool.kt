package com.thunder.thunderplane.tool

import android.app.Activity
import android.view.View
import android.widget.ImageView
import com.thunder.thunderplane.R
import com.thunder.thunderplane.tool.ViewTool.getJetBullet
import com.thunder.thunderplane.tool.ViewTool.getRandomUFOView

object ViewTool {

    const val BULLET_LEVEL_1 = 111
    const val BULLET_LEVEL_2 = 222
    const val BULLET_LEVEL_3 = 333
    const val BULLET_LEVEL_4 = 4444

    fun Activity.getUpgradeItem(): View {
        return View.inflate(this, R.layout.upgrade_item_layout, null)
    }

    fun Activity.getRandomUFOView(): View {
        val viewList = mutableListOf<Int>()
        viewList.add(R.drawable.ufo)
        viewList.add(R.drawable.ufo1)
        viewList.add(R.drawable.ufo2)
        viewList.add(R.drawable.ufo3)

        val view = View.inflate(this, R.layout.ufo_layout, null)
        val img = view.findViewById<ImageView>(R.id.ufo)
        img.setImageResource(viewList[(0 until viewList.size - 1).random()])
        return view
    }

    fun Activity.getSmallBoss(): View {
        val list = mutableListOf<Int>()
        list.add(R.drawable.ufo_boss)
        list.add(R.drawable.ufo_boss1)
        val view = View.inflate(this, R.layout.ufo_boss_layout, null)
        val img = view.findViewById<ImageView>(R.id.boss)
        img.setImageResource(list[(0 until list.size - 1).random()])
        return view
    }

    fun Activity.getUFoBullet(): View {
        return View.inflate(this, R.layout.ufo_bullet_layout, null)
    }

    fun Activity.getBossBullet(): View {
        return View.inflate(this, R.layout.boss_bullet_layout, null)
    }

    fun Activity.getJetBullet(tag: Any): View {
        return when (tag) {
            BULLET_LEVEL_1 -> {
                View.inflate(this, R.layout.bullet_layout, null)
            }
            BULLET_LEVEL_2 -> {
                View.inflate(this, R.layout.bullet_level2_layout, null)
            }
            BULLET_LEVEL_3 -> {
                View.inflate(this, R.layout.bullet_level3_layout, null)
            }
            else -> {
                View.inflate(this, R.layout.bullet_level4_layout, null)
            }
        }
    }

    fun Activity.getExplodeView(): View {
        return View.inflate(this, R.layout.explode_layout, null)
    }

    fun upgradeBulletLevel(tag: Any): Int {
        return when (tag) {
            BULLET_LEVEL_1 -> {
                BULLET_LEVEL_2
            }
            BULLET_LEVEL_2 -> {
                BULLET_LEVEL_3
            }
            BULLET_LEVEL_3 -> {
                BULLET_LEVEL_4
            }
            else -> {
                BULLET_LEVEL_4
            }
        }
    }

    fun getDamage(tag: Any): Int {
        return when (tag) {
            BULLET_LEVEL_1 -> {
                10
            }
            BULLET_LEVEL_2 -> {
                20
            }
            BULLET_LEVEL_3 -> {
                30
            }
            else -> {
                10
            }
        }
    }

}