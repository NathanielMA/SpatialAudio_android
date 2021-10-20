package com.example.spatialaudio.variables

import java.net.DatagramSocket
import java.net.MulticastSocket

/**
 * Hyper IMU socket
 */
var IMUSocket: DatagramSocket = DatagramSocket(9001)

/**
 *  Multicast Socket on port 8010
 */
lateinit var socketMultiConnect: MulticastSocket

/**
 * Audio port of self. Ranging from set Port -> Port + 7.
 */
var portAudio = 7777

/**
 * DatagramSocket used for sending audio data over multicast network.
 */
val socketSendAudio = DatagramSocket()

/**
 * Int variable for incrementing Port number within Functions
 */
var incPort: Int = 7777
