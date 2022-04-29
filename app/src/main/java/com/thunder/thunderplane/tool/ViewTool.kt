package com.thunder.thunderplane.tool

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.thunder.thunderplane.R
import com.thunder.thunderplane.tool.ViewTool.getJetBullet
import com.thunder.thunderplane.tool.ViewTool.getRandomUFOView

object ViewTool {

    const val BULLET_LEVEL_1: Int = 111
    private const val BULLET_LEVEL_2 = 222
    private const val BULLET_LEVEL_3 = 333
    private const val BULLET_LEVEL_4 = 4444
    const val BULLET_LEVEL_5 = 555

    fun Activity.getUpgradeItem(): View {
        return View.inflate(this, R.layout.upgrade_item_layout, null)
    }

    fun Context.getRandomUFOView(): View {
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

    fun Context.getSmallBoss(): View {
        val list = mutableListOf<Int>()
        list.add(R.drawable.ufo_boss)
        list.add(R.drawable.ufo_boss1)
        val view = View.inflate(this, R.layout.ufo_boss_layout, null)
        val img = view.findViewById<ImageView>(R.id.boss)
        img.setImageResource(list[(0 until list.size).random()])
        return view
    }
    fun Activity.getBigBoss(): View {
        val list = mutableListOf<Int>()
        list.add(R.drawable.big_boss1)
        val view = View.inflate(this, R.layout.ufo_big_boss_layout, null)
        val img = view.findViewById<ImageView>(R.id.boss)
        img.setImageResource(list[(0 until list.size).random()])
        return view
    }


    fun Context.getUFoBullet(): View {
        return View.inflate(this, R.layout.ufo_bullet_layout, null)
    }

    fun Context.getBossBullet(): View {
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
            BULLET_LEVEL_4 ->{
                View.inflate(this, R.layout.bullet_level4_layout, null)
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
                BULLET_LEVEL_5
            }
        }
    }

    fun getDamage(tag: Any): Int {
        return when (tag) {
            BULLET_LEVEL_1 -> {
                20
            }
            BULLET_LEVEL_2 -> {
                30
            }
            BULLET_LEVEL_3 -> {
                40
            }
            BULLET_LEVEL_4 ->{
                50
            }
            else -> {
               30
            }
        }
    }

}