package com.example.spatialaudio.variables

import java.io.ByteArrayOutputStream
import java.net.DatagramSocket

/**
 * Audio format that has been constructed with specific parameters:
 *
 *      AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
 *                  sampleRate: 44100F,
 *                  sampleSizeInBits: 16,
 *                  channels: 2,
 *                  frameSize: 4,
 *                  frameRate: 44100F,
 *                  bigEndian: true)
 */
//private val format = AudioFormat(
//    44100F,
//    16,
//    2,
//    4,
//    44100F,
//    true)

/**
 * List of 8 ByteArrayOutputStreams used for storing operator audio data for use with
 * OpenAL.
 */
private val outDataBuffer = listOf<ByteArrayOutputStream>(
    ByteArrayOutputStream(),
    ByteArrayOutputStream(),
    ByteArrayOutputStream(),
    ByteArrayOutputStream(),
    ByteArrayOutputStream(),
    ByteArrayOutputStream(),
    ByteArrayOutputStream(),
    ByteArrayOutputStream()
)


/**
 * List of dedicated DatagramSockets available for use for up to 8 operators.
 *
 * Sockets range from ports set Port -> Port + 7.
 */
private var socketRecAudio = mutableListOf<DatagramSocket>()

/**
 * Int variable which is used to store the overload of the alGenBuffers()
 */
private var buffer3D: Int = 0

/**
 * Stores data received from the TARGETDATALINE
 */
private var numBytesRead: Int = 0