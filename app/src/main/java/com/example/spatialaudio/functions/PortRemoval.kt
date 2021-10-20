package com.example.spatialaudio.functions

import com.example.spatialaudio.variables.*

object PortRemoval {
    fun removePort(Port: String){
        for (i in potentialOP.indices) {
            when (Port.toInt()) {
                incPort + i -> {
                    if (!operators.containsKey(potentialOP[i])) {
                        portsAudio.remove(Port)
                    }
                }
            }
        }
    }
}