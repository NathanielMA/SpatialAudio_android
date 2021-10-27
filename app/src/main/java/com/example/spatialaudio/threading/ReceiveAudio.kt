package com.example.spatialaudio.threading

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import com.example.spatialaudio.variables.bufferReceive
import com.example.spatialaudio.variables.portAudio
import com.example.spatialaudio.variables.sampleRate
import java.net.DatagramPacket
import java.net.DatagramSocket

/*
TODO:
 - Add mute button
 - Utilize portAudio assignments
 - Ensure ReceiveAudio starts to run only AFTER a datagramSocket for portAudio has been created after portAudio is assigned (see AudioPortAssign function)
 */

class ReceiveAudio: Thread() {
    override fun run() {
        val socket = DatagramSocket(portAudio)
        val buffer = ByteArray(bufferReceive)

        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_STEREO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferReceive,
            AudioTrack.MODE_STREAM
        )

        audioTrack.playbackRate = sampleRate

        audioTrack.play()

        while (true) {
            val receiveAudio = DatagramPacket(buffer, buffer.size)

            socket.receive(receiveAudio)

            audioTrack.write(receiveAudio.data, 0, receiveAudio.length)
        }
    }
}