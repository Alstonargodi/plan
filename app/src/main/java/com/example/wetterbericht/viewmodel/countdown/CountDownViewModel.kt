package com.example.wetterbericht.viewmodel.countdown

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class CountDownViewModel: ViewModel() {

    private lateinit var countDownTimer: CountDownTimer
    private val initialTime = MutableLiveData<Long>()
    private val durationTime = MutableLiveData<Long>()

    val currentTimeString = Transformations.map(durationTime){ time ->
        DateUtils.formatElapsedTime(time / 1000)
    }

    fun setInitialCountDown(time : Long){
        val convertTime = time * 60 * 1000
        initialTime.value = convertTime
        durationTime.value = convertTime

        countDownTimer = object : CountDownTimer(convertTime,1000){
            override fun onTick(timeRemind: Long) {
                durationTime.value = timeRemind
            }

            override fun onFinish() {
                resetTimer()
            }
        }
    }

    fun start(){
        countDownTimer.start()
    }

    fun resetTimer(): Boolean{
        countDownTimer.cancel()
        durationTime.value = initialTime.value
        return true
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer.cancel()
    }






}