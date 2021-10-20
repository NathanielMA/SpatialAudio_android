package com.example.spatialaudio.threading

import com.example.spatialaudio.variables.*
import com.example.spatialaudio.functions.CoordinateAllocation.allocateCoords
import com.example.spatialaudio.functions.PortAllocation.allocatePort
import java.net.DatagramPacket
import com.example.spatialaudio.functions.DistanceCalculation.operatorDistance as OperatorDistance
import com.example.spatialaudio.functions.AzimuthCalculation.azimuthCalc as AzimuthCalc
import com.example.spatialaudio.functions.NoNewOp.timeout as noNewOp

class ReceiveOperatorData: Thread() {
    override fun run() {
        while (true) {
            val buffer2 = ByteArray(1024)
            val response2 = DatagramPacket(buffer2, 1024)

            socketMultiConnect.receive(response2)

            val data2 = response2.data
            opDataDataString = String(data2, 0, data2.size)

            when {
                """OP-DATA: """.toRegex()
                    .containsMatchIn(opDataDataString) -> {
                    /** Variables used to store and recognize parsed data from received packets
                     * Variables will Regex:
                     *      operator IP, Name, Port and Coordinates
                     */
                    val opIP =
                        """(?<=IP: )(\d+)\.(\d+)\.(\d+)\.(\d+)""".toRegex()
                            .find(opDataDataString)?.value.toString()

                    if (opIP != self.OperatorIP) {

                        val opPort = """(?<=PORT_AUDIO: )\d+""".toRegex()
                            .find(opDataDataString)?.value.toString()
                        val opCoords = """-?(\d+)\.(\d+)""".toRegex()
                        val patt = opCoords.findAll(opDataDataString)

                        var i = 0
                        patt.forEach { f ->
                            opGPS[i] = f.value
                            i++
                        }

                        //Allocate received coordinates to correct operator
                        allocateCoords(opPort, opGPS[2].toDouble(), opGPS[3].toDouble(), 0.0)


                        for (key in operators.keys) {
                            if (operators[key]?.OperatorIP != self.OperatorIP) {

                                // Calculate Azimuth between self and operator
                                operators[key]?.OperatorAzimuth = AzimuthCalc(
                                    self.OperatorLongitude,
                                    self.OperatorLatitude,
                                    operators[key]!!.OperatorLongitude,
                                    operators[key]!!.OperatorLatitude,
                                    self.OperatorNose
                                )

                                //Calculate distance between self and operator
                                operators[key]?.OperatorDistance = OperatorDistance(
                                    self.OperatorLongitude,
                                    self.OperatorLatitude,
                                    operators[key]!!.OperatorLongitude,
                                    operators[key]!!.OperatorLatitude
                                )
                            }

                            if (!portsAudio.contains(opPort)) {
                                if (!opDetected && !operators.containsKey(opPort)) {
                                    noNewOp()
                                }
                            }
                            if (opNotFound) {
                                portsAudio.add(opPort)
                                addresses.add(opIP)
                                allocatePort(opIP, opPort)
                                opNotFound = false
                            }

//                                    operatorTimeOut(opIP)
                        }
                    }
                }
            }
        }
    }
}