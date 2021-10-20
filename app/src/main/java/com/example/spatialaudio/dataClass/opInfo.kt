package com.example.spatialaudio.dataClass

/**Data class that stores operator information
 * Stores:
 *      IP
 *      Port
 *      Host Name
 *      Longitude, Latitude, Distance Azimuth and Nose
 * Note:
 *      offset, activeTime, isActive are used for dynamic port removal
 */
data class opInfo(var OperatorIP: String, var OperatorPort: String = "") {
    var OperatorName: String = ""
    var OperatorLongitude: Double = 0.0
    var OperatorLatitude: Double = 0.0
    var OperatorNose: Double = 0.0
    var OperatorAzimuth: Double = 0.0
    var OperatorDistance: Double = 0.0
    var offset: Int = 0
    var activeTime: Int = 0
    var isActive: Boolean = false
}