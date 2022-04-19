package com.thunder.thunderplane.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.thunder.thunderplane.R
import com.thunder.thunderplane.tool.Tool.getPixel

class GameOverDialog(private val score: Long) : DialogFragment() {

    private lateinit var fragmentActivity : FragmentActivity

    private lateinit var onGameOverDialogClickListener: OnGameOverDialogClickListener

    fun setOnGameOverDialogClickListener(onGameOverDialogClickListener: OnGameOverDialogClickListener){
        this.onGameOverDialogClickListener = onGameOverDialogClickListener
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.fragmentActivity = context as FragmentActivity
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        val inflater = fragmentActivity.layoutInflater
        val view = inflater.inflate(R.layout.game_over_dialog_layout, null)
        val dialog = Dialog(fragmentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        initView(view)
        dialog.setCancelable(false)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = window?.attributes
        wlp?.width = fragmentActivity.getPixel(350)
        wlp?.height = fragmentActivity.getPixel(300)
        window?.attributes = wlp
        return dialog
    }

    private fun initView(view: View) {
        val gameOver = view.findViewById<ConstraintLayout>(R.id.game_over_close)
        val restart = view.findViewById<ConstraintLayout>(R.id.game_over_restart)
        val tvScore = view.findViewById<TextView>(R.id.game_over_score)
        tvScore.text = getString(R.string.final_score) + score
        gameOver.setOnClickListener {
            onGameOverDialogClickListener.onCloseGame()
            dismiss()
        }
        restart.setOnClickListener {
            onGameOverDialogClickListener.onRestartGame()
            dismiss()
        }
    }


    interface OnGameOverDialogClickListener{
        fun onCloseGame()
        fun onRestartGame()
    }

}