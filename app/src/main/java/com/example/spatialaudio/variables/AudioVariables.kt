package com.example.spatialaudio.variables

import android.media.*
import java.io.ByteArrayOutputStream
import java.net.DatagramSocket

val sampleRate = 44100
val channelConfig = AudioFormat.CHANNEL_IN_STEREO
val audioFormat = AudioFormat.ENCODING_PCM_16BIT
val bufferReceive = AudioRecord.getMinBufferSize(AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_MUSIC), AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT)
val bufferSend = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

var status: Boolean = true


/**
 * List of 8 ByteArrayOutputStreams used for storing operator audio data for use with
 * OpenAL.
 */
val outDataBuffer = listOf<ByteArrayOutputStream>(
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
var socketRecAudio = mutableListOf<DatagramSocket>()

/**
 * Int variable which is used to store the overload of the alGenBuffers()
 */
var buffer3D: Int = 0

/**
 * Stores data received from the TARGETDATALINE
 */
var numBytesRead: Int = 0