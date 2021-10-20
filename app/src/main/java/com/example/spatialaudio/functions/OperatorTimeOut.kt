package com.example.spatialaudio.functions

import com.example.spatialaudio.variables.*

object OperatorTimeOut {
    fun operatorTimeOut(IP: String) {
        for(key in operators.keys){
            if (operators[key]!!.OperatorIP == IP && IP != self.OperatorIP) {
                if(!operators[key]!!.isActive) {
                    operators[key]?.isActive = true
                }
                operators[key]!!.activeTime += 1

                operators[key]!!.offset = self.activeTime - operators[key]!!.activeTime
            }
        }
    }
}