package com.example.spatialaudio.threading

import com.example.spatialaudio.variables.*
import com.example.spatialaudio.functions.CoordinateAllocation.allocateCoords
import com.example.spatialaudio.functions.IMUData.getData
import java.net.DatagramPacket
import java.net.InetAddress

class SendMyData: Thread() {
    override fun run() {
        while (true) {
            sleep(1000)

            myData = getData(IMUSocket)
            self.activeTime += 1

            if(portAudio.toString() == self.OperatorPort) {
                allocateCoords(portAudio.toString(), myData[0], myData[1], myData[2])
            }
            val dataString2 = "OP-DATA: IP: $hostAdd PORT_AUDIO: $portAudio COORDS: $myData--"

            val datagramPacket = DatagramPacket(
                dataString2.toByteArray(),
                dataString2.toByteArray().size,
                InetAddress.getByName("230.0.0.0"),
                multiCastPort.toInt()
            )
            socketMultiConnect.send(datagramPacket)

//                    try{
//                        for (key in operators.keys) {
//                            if ((_self.activeTime - operators[key]?.activeTime!!) - operators[key]?.offset!! > 1 && operators[key]?.OperatorIP != _self.OperatorIP) {
//                                operators[key]!!.isActive = false
//                                portsAudio.remove(operators[key]!!.OperatorPort)
//                                addresses.remove(operators[key]?.OperatorIP)
//                                operators.remove(key)
//                            }
//                        }
//                    } catch (e: ConcurrentModificationException){
//
//                    }
        }
    }
}