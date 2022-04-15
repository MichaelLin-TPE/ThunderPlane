package com.thunder.thunderplane.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thunder.thunderplane.dialog.GameOverDialog

open class BaseActivity : AppCompatActivity() {

    fun showGameOverDialog(onGameOverDialogClickListener: GameOverDialog.OnGameOverDialogClickListener) {
        if (isGameOverDialogShowing()) {
            return
        }
        val dialog = GameOverDialog()
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

    }
}