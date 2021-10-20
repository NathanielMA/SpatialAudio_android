package com.example.spatialaudio.functions

object DistanceCalculation {
    fun operatorDistance(myLongitude: Double, myLatitude: Double, opLongitude: Double, opLatitude: Double): Double{
        val dLat: Double = Math.toRadians(opLatitude - myLatitude)
        val dLong: Double = Math.toRadians(opLongitude - myLongitude)
        val a = Math.pow(Math.sin(dLat/2), 2.0) + Math.cos(myLatitude) * Math.cos(opLatitude) * Math.pow(Math.sin(dLong/2), 2.0)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val R = 6300000
        val feet = ((R * c) * 100)/(2.54 * 12)

        return (feet)
    }
}