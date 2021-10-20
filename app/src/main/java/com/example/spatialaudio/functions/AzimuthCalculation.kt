package com.example.spatialaudio.functions

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object AzimuthCalculation {
    fun azimuthCalc(myLongitude: Double, myLatitude: Double, opLongitude: Double, opLatitude: Double, Nose: Double): Double {
        val endLongitude: Double = Math.toRadians(opLongitude - myLongitude)
        val endLatitude: Double = Math.toRadians(opLatitude)
        val startLatitude = Math.toRadians(myLatitude)
        var phi = Math.toDegrees(atan2(sin(endLongitude) * cos(endLatitude), cos(startLatitude) * sin(endLatitude) - sin(startLatitude) * cos(endLatitude) * cos(endLongitude)))

        (phi + 360) % 360

        if(Nose < phi){
            phi = phi - Nose
        } else if(Nose > phi && ((Nose - phi) < 180)){
            phi = Nose - phi
        } else if (Nose > phi && ((Nose - phi) > 180)){
            phi = 360 - (Nose - phi)
        }

        return(phi)
    }
}