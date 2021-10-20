package com.example.spatialaudio.variables

import com.example.spatialaudio.dataClass.opInfo

/**
 * List of connected ports
 */
val portsAudio = mutableSetOf<String>()

/**
 * Int variable for storing the designated Hpper IMU port.
 */
var IMUPort: Int = 0

/**
 * Detects whether self has been notified for being unable to receive Hyper IMU data.
 */
var notified: Boolean = false

/**
 * This String is used to display information for connecting Hyper IMU if it is not running
 */
lateinit var infoString: String

/**
 * Data variable which handles TargetDataLine data
 */
private lateinit var data: ByteArray

/**
 * Used alongside the comparator variable to detect when new operators join or leave the server.
 */
private var currentOps = mutableMapOf<String, opInfo>()

/**
 * Used alongside the currentOps variable to detect when new operates join or leave the server.
 */
private var comparator: Int = 0

/**
 * Buffer size in Bytes for storing audio data
 */
private const val buffer = 1024

/**
 * List of IP's
 */
val addresses = mutableSetOf<String>()

/**
 * Detects new connection request
 */
var opDetected = false

/**
 * Determines whether server has been initialized.
 * If server has not been initialized, this tells the user that they are
 * the first to initialize the server.
 */
var timeOutOp = false

/**
 * Determines if self has been initialized.
 */
var selfAdded = false

/**
 * Determines if operator is already contained within data base.
 */
var opNotFound = false

/**
 * Mutable list which is used for storing GPS data until it is saved within a data class.
 */
var opGPS = mutableListOf<String>("","","","","")

/**
 * Determines whether the thread which sends off own audio has been suspended
 */
private var suspended: Boolean = false

/**
 * IP of own device
 */
var hostAdd: String = ""

/**
 * List of operators connected to server
 */
var operators = mutableMapOf<String, opInfo>()

/**
 * List of all potential operators to be contained in data base.
 */
val potentialOP = listOf<String>("OP1","OP2","OP3","OP4","OP5","OP6","OP7","OP8")

var azimuthData = arrayOf<String>("","","","","","")
var Longitude: Double = 0.0
var Latitude: Double = 0.0
var Nose: Double = 0.0
var tcanc = 0
var multiCastPort: String = "0"
var receiverDataString: String = ""
var opDataDataString: String = ""
var updateRate = 200L
var myData = listOf<Double>(0.0, 0.0, 0.0)
lateinit var self: opInfo