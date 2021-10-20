package com.example.spatialaudio.functions

import com.example.spatialaudio.variables.*

object CoordinateAllocation {
    fun allocateCoords(Port: String, Longitude: Double, Latitude: Double, Nose: Double) {
        for (i in potentialOP.indices){
            when (Port.toInt()) {
                portAudio + i -> {
                    operators[potentialOP[i]]?.OperatorLongitude = Longitude
                    operators[potentialOP[i]]?.OperatorLatitude = Latitude
                    operators[potentialOP[i]]?.OperatorNose = Nose
                }
            }
        }
    }
}