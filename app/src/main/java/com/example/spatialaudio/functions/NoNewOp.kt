package com.example.spatialaudio.functions

import com.example.spatialaudio.variables.*
import java.util.*

object NoNewOp {
    var mTimer = Timer()
    fun timeout() {
        val timertask: TimerTask = object : TimerTask() {
            override fun run() {
                timeOutOp = true
            }
        }
        mTimer = Timer()
        mTimer.schedule(timertask, 5000)
    }
}