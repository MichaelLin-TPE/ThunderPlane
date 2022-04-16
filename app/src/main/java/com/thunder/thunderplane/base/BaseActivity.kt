package com.thunder.thunderplane.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thunder.thunderplane.dialog.GameOverDialog
import com.thunder.thunderplane.tool.MusicTool
import com.thunder.thunderplane.tool.MusicTool.initMusic

open class BaseActivity : AppCompatActivity() {

    fun showGameOverDialog(
        score: Long,
        onGameOverDialogClickListener: GameOverDialog.OnGameOverDialogClickListener
    ) {
        if (isGameOverDialogShowing()) {
            return
        }
        val dialog = GameOverDialog(score)
        dialog.show(supportFragmentManager, "dialog")
        dialog.setOnGameOverDialogClickListener(object :
            GameOverDialog.OnGameOverDialogClickListener {
            override fun onCloseGame() {
                onGameOverDialogClickListener.onCloseGame()
            }

            override fun onRestartGame() {
                onGameOverDialogClickListener.onRestartGame()
            }
        })
    }

    private fun isGameOverDialogShowing(): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag("dialog")
        return fragment != null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.initMusic()
        MusicTool.playBgMusic()
    }
}