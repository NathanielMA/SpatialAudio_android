package com.example.spatialaudio.functions

import com.example.spatialaudio.variables.*
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket

object IMUData {
    fun getData(IMUSocket: DatagramSocket): List<Double> {
        val dataString: String
        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)
        IMUSocket.setSoTimeout(5000)

        try {
            IMUSocket.receive(packet)
            val message = packet.data
            dataString = String(message, 0, message.size)
            val azimuthRegex = """-?(\d+)\.\d+""".toRegex()

            val patt = azimuthRegex.findAll(dataString)

            var i = 0

            patt.forEach { f ->
                azimuthData[i] = f.value

                if (i > 5) {
                    i = 0
                }
                i++
            }
            try {
                Longitude = azimuthData[4].toDouble()
                Latitude = azimuthData[3].toDouble()
                Nose = azimuthData[0].toDouble()
                self.OperatorLongitude = Longitude
                self.OperatorLatitude = Latitude
                self.OperatorNose = Nose
            } catch (e: NumberFormatException) {

                Longitude = 0.0
                Latitude = 0.0
                Nose = 0.0
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return listOf(Longitude, Latitude, Nose)
    }
}