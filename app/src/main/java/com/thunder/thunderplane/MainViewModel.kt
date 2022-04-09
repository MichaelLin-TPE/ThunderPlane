package com.thunder.thunderplane

import android.app.Activity
import android.view.View
import androidx.lifecycle.*
import com.thunder.thunderplane.bean.BulletMoveData
import com.thunder.thunderplane.bean.ControlData
import com.thunder.thunderplane.bean.JetMoveData
import com.thunder.thunderplane.bean.TargetMoveData
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.UITool
import com.thunder.thunderplane.tool.UITool.getBulletView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _moveJetLiveData = MutableLiveData<JetMoveData>()
    private val currentJetData get() = _moveJetLiveData.value!!
    val moveJetLiveData: LiveData<JetMoveData> = _moveJetLiveData
    private var jetX = 0f
    private var jetY = 0f

    private val _bulletLiveData = MutableLiveData<View>()
    private val currentBulletData get() = _bulletLiveData.value!!
    val bulletLiveData: LiveData<View> = _bulletLiveData


    private val _bulletMoveLiveData = MutableLiveData<BulletMoveData>()
    private val currentBulletMoveData = _bulletMoveLiveData
    val bulletMoveLiveData :LiveData<BulletMoveData> = _bulletMoveLiveData


    fun onMoveJefListener(
        rawX: Float,
        rawY: Float,
        jetWidth: Int,
        jetHeight: Int,
        right: Int,
        left: Int,
        top: Int,
        bottom: Int
    ) {
        val jetMoveX = rawX + jetX
        val jetMoveY = rawY + jetY

        if ((jetMoveX + jetWidth) > UITool.getScreenWidth()) {
            return
        }
        if (jetMoveX < 0) {
            return
        }
        if ((jetMoveY + jetHeight) > UITool.getScreenHeight()) {
            return
        }

        if (jetMoveY < 0) {
            return
        }
        _moveJetLiveData.value = JetMoveData(jetMoveX, jetMoveY,right,left,top,bottom)
    }

    fun setJetXY(jetX: Float, jetY: Float) {
        this.jetX = jetX
        this.jetY = jetY
    }

    fun startShooting() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                _bulletLiveData.postValue((MyApplication.instance.applicationContext as Activity).getBulletView())

                while (isActive) {

                    currentBulletData.post {
                        val centerX =
                            (currentJetData.jetX + ((currentJetData.right - currentJetData.left) / 2)) - ((currentBulletData.right - currentBulletData.left) / 2)
                        _bulletMoveLiveData.postValue(BulletMoveData(centerX,currentJetData.jetY - 100f))
                    }

                    delay(100)
                }

                delay(500)
            }
        }
    }


    class MainViewModelFactory() : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel() as T
        }
    }

}