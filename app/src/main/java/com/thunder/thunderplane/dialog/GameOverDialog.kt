package com.thunder.thunderplane.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.thunder.thunderplane.R
import com.thunder.thunderplane.tool.UITool
import com.thunder.thunderplane.tool.UITool.getPixel

class GameOverDialog : DialogFragment() {

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

        val inflater = fragmentActivity.layoutInflater
        val view = inflater.inflate(R.layout.game_over_dialog_layout, null)
        val dialog = Dialog(fragmentActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)

        dialog.setCancelable(false)
        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val wlp = window?.attributes
        wlp?.width = fragmentActivity.getPixel(300)
        wlp?.height = fragmentActivity.getPixel(150)
        window?.attributes = wlp
        return dialog
    }


    interface OnGameOverDialogClickListener{
        fun onCloseGame()
        fun onRestartGame()
    }

}