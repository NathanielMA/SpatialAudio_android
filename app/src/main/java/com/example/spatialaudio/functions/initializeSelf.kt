package com.example.spatialaudio.functions

import com.example.spatialaudio.dataClass.opInfo
import com.example.spatialaudio.variables.*

object initializeSelf {
    fun self(): opInfo {
        return opInfo(OperatorIP = hostAdd)
    }
}