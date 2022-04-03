package com.thunder.thunderplane

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thunder.thunderplane.bean.ControlData
import com.thunder.thunderplane.bean.JetMoveData
import com.thunder.thunderplane.bean.TargetMoveData
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.UITool

class MainViewModel : ViewModel() {

    val addControlCircleLiveData = MutableLiveData<ControlData>()
    val moveJetLiveData = MutableLiveData<JetMoveData>()
    val moveTargetLiveData = MutableLiveData<TargetMoveData>()

    private var jetX = 0f
    private var jetY = 0f

    private var targetX = 0f
    private var targetY = 0f

    private var controlLeft = 0f
    private var controlRight = 0f
    private var controlTop = 0f
    private var controlBottom = 0f

    private var targetStartX = 0f
    private var targetStartY = 0f

    private var jetMoveX = 0f
    private var jetMoveY = 0f

    private var jetWidth = 0
    private var jetHeight = 0

    fun onAddControlCircleListener(isShow: Boolean, x: Float, y: Float) {
        addControlCircleLiveData.value = ControlData(isShow,x,y)
    }

    fun onMoveJefListener(rawX: Float, rawY: Float, jetWidth: Int, jetHeight: Int) {
        this.jetHeight = jetHeight
        this.jetWidth = jetWidth
        val jetMoveX = rawX + jetX
        val jetMoveY = rawY + jetY
        moveJet(jetMoveX,jetMoveY)

    }

    private fun moveJet(jetMoveX: Float, jetMoveY: Float) {
        MichaelLog.i("jetX : $jetMoveX , jetY : $jetMoveY")

        if ((jetMoveX + jetWidth) > UITool.getScreenWidth()){
            return
        }
        if (jetMoveX < 0){
            return
        }
        if ((jetMoveY + jetHeight) > UITool.getScreenHeight()){
            return
        }

        if (jetMoveY < 0){
            return
        }
        moveJetLiveData.value = JetMoveData(jetMoveX,jetMoveY)
    }

    fun setJetXY(jetX: Float, jetY: Float) {
        this.jetX = jetX
        this.jetY = jetY
    }

    fun onMoveTargetListener(rawX: Float, rawY: Float, targetWidth: Int, targetHeight: Int) {
        val moveX = rawX + targetX
        val moveY = rawY + targetY

        if (targetStartX == 0f && targetStartY == 0f){
            targetStartX = moveX
            targetStartY = moveY
        }else{
            targetStartX = moveX - targetStartX
            targetStartY = moveY - targetStartY
        }

        if (moveY < controlTop){
            moveJet(jetMoveX + targetStartX , jetMoveY + targetStartY)
            return
        }
        if ((moveY + targetHeight) > controlBottom){
            moveJet(jetMoveX + targetStartX , jetMoveY + targetStartY)

            return
        }
        if ((moveX + targetWidth) > controlRight){
            moveJet(jetMoveX + targetStartX , jetMoveY + targetStartY)

            return
        }
        if (moveX < controlLeft){
            moveJet(jetMoveX + targetStartX , jetMoveY + targetStartY)
            return
        }


        moveTargetLiveData.value = TargetMoveData(moveX,moveY)

    }

    fun setTarget(x: Float, y: Float) {
        targetX = x
        targetY = y
    }

    fun setControlViewWidthHeight(controlLeft: Float, controlTop: Float, right: Float, bottom: Float) {
        this.controlLeft = controlLeft
        this.controlTop = controlTop
        this.controlRight = right
        this.controlBottom = bottom
    }


    class MainViewModelFactory() : ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }

}