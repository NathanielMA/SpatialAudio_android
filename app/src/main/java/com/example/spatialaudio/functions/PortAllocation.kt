package com.example.spatialaudio.functions

import com.example.spatialaudio.dataClass.opInfo
import com.example.spatialaudio.variables.*

object PortAllocation {
    fun allocatePort(IP: String, Port: String){
        for(i in potentialOP.indices) {
            when (Port.toInt()) {
                incPort + i -> {
                    operators[potentialOP[i]] = opInfo(OperatorIP = IP)
                    operators[potentialOP[i]]?.OperatorPort = Port
                    operators[potentialOP[i]]?.OperatorName = potentialOP[i]
                }
            }
        }
    }
}