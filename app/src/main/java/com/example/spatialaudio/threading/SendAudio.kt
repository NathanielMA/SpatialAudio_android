package com.example.spatialaudio.threading

import android.annotation.SuppressLint
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.example.spatialaudio.variables.*
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.properties.Delegates

class SendAudio: Thread() {
    @SuppressLint("MissingPermission")
    override fun run() {
        while (true) {
            try{
                val sendAudio = DatagramSocket()
                var port by Delegates.notNull<Int>()

                val buffer = ByteArray(bufferSend)
                var sendPacket: DatagramPacket

                val recorder = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    bufferSend
                )

                recorder.startRecording()

                while(status == true){

                    for(i in 0 until addresses.size) {
                        for (key in operators.keys) {
                            if (addresses.elementAtOrNull(i) == operators[key]?.OperatorIP){
                                port = operators[key]?.OperatorPort?.toInt()!!
                            }
                        }

                        sendPacket = DatagramPacket(
                            buffer,
                            buffer.size,
                            InetAddress.getByName(addresses.elementAtOrNull(i)),
                            port
                        )

                        sendAudio.send(sendPacket)
                    }
                }

            } catch (e: UnknownHostException){
                Log.e("VS", "UnkownHostException")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.e("VS", "IOException")
                e.printStackTrace()
            }
        }
    }
}