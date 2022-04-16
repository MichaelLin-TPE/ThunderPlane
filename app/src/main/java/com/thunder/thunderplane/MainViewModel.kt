package com.thunder.thunderplane

import android.view.View
import androidx.lifecycle.*
import com.thunder.thunderplane.bean.BulletData
import com.thunder.thunderplane.bean.ControlData
import com.thunder.thunderplane.bean.JetMoveData
import com.thunder.thunderplane.bean.TargetMoveData
import com.thunder.thunderplane.log.MichaelLog
import com.thunder.thunderplane.tool.UITool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainViewModel(val repository: MainRepository) : ViewModel() {

    private val _moveJetLiveData = MutableLiveData<JetMoveData>()
    private val currentJetData get() = _moveJetLiveData.value!!
    val moveJetLiveData: LiveData<JetMoveData> = _moveJetLiveData
    private var jetX = 0f
    private var jetY = 0f

    private val _scoreLiveData = MutableLiveData<Long>(0)
    private val currentScore get() =  _scoreLiveData.value!!
    val scoreLiveData : LiveData<Long> = _scoreLiveData


    private val _createSmallBossLiveData = MutableLiveData<Boolean>()
    val createSmallBossLiveData : LiveData<Boolean> = _createSmallBossLiveData

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
        _moveJetLiveData.value = JetMoveData(jetMoveX, jetMoveY, right, left, top, bottom)
    }

    fun setJetXY(jetX: Float, jetY: Float) {
        this.jetX = jetX
        this.jetY = jetY
    }

    fun addScore(score: Int) {
        val num = currentScore + score
        _scoreLiveData.value = num
    }

    fun reStartScore() {
        _scoreLiveData.value = 0
    }

    fun onCreateSmallBoss() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive){
                MichaelLog.i("score : $currentScore")
                if (currentScore != 0L && currentScore % 2000 == 0L){
                    viewModelScope.launch(Dispatchers.Main) {
                        _createSmallBossLiveData.value = true
                    }
                    delay(2000)
                    continue
                }
                delay(50)
            }
        }


    }

    class MainViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainViewModel(mainRepository) as T
        }
    }

}