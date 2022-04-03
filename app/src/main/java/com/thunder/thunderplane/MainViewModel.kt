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



    fun onAddControlCircleListener(isShow: Boolean, x: Float, y: Float) {
        MichaelLog.i("isShow $isShow")
        addControlCircleLiveData.value = ControlData(isShow,x,y)
    }

    fun onMoveJefListener(rawX: Float, rawY: Float, jetWidth: Int, jetHeight: Int) {
        val jetMoveX = rawX + jetX
        val jetMoveY = rawY + jetY

        if ((jetMoveX + jetWidth) > UITool.getScreenWidth()){
            return
        }
        if (jetMoveX < 0){
            return
        }
        if ((jetMoveY + jetHeight) > UITool.getScreenHeight()){
            return
        }
        MichaelLog.i("jet x : ${(jetMoveY + jetWidth)} , screen height : ${UITool.getScreenHeight()}")

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

        if (moveY < controlTop){
            return
        }
        if ((moveY + targetHeight) > controlBottom){
            return
        }
        if ((moveX + targetWidth) > controlRight){
            return
        }
        if (moveX < controlLeft){
            return
        }


        moveTargetLiveData.value = TargetMoveData(moveX,moveY)

    }

    fun setTarget(x: Float, y: Float) {
        targetX = x
        targetY = y
    }

    fun setControlViewWidthHeight(controlLeft: Float, controlTop: Float, right: Float, bottom: Float) {
        MichaelLog.i("left : $controlLeft , right : $right , top : $controlTop , bottom : $bottom")
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