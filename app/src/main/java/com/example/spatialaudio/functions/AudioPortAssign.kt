package com.example.spatialaudio.functions

import com.example.spatialaudio.dataClass.opInfo
import com.example.spatialaudio.functions.PortAllocation.allocatePort
import java.util.*
import com.example.spatialaudio.variables.*

object AudioPortAssign {
    fun assignPort(_self: opInfo) {
        var timer = Timer()
        val timertask: TimerTask = object : TimerTask() {
            override fun run() {
                if (portsAudio.contains(portAudio.toString()) && !selfAdded) {
                    for (i in 0 until portsAudio.size) {
                        if (portsAudio.contains(portAudio.toString())) {
                            portAudio += 1
                        } else if ((portAudio - incPort) >= 8){
                            break
                        }
                    }
                    portsAudio.add(portAudio.toString())
                    _self.OperatorPort = portAudio.toString()
                    allocatePort(hostAdd, portAudio.toString())
                    selfAdded = true
                }
            }
        }
        timer = Timer()
        timer.schedule(timertask, (1000..3000).random().toLong())
    }
}