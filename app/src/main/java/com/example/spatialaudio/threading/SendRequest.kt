package com.example.spatialaudio.threading

import com.example.spatialaudio.variables.*
import com.example.spatialaudio.functions.PortAllocation.allocatePort
import java.net.DatagramPacket
import java.net.InetAddress

class SendRequest: Thread() {
    override fun run() {
        while (true) {
            // Initialize first operator (self) on server
            sleep(1000)
            if (timeOutOp) {
                sleep(1000)
                if (addresses.size == 1 && addresses.contains(self.OperatorIP)) {
                    /** Send own information over server
                     * This is used until at least one operator joins
                     */
                    val dataString = "OP REQUEST: IP: $hostAdd PORT_AUDIO: $portAudio"

                    val datagramPacket = DatagramPacket(
                        dataString.toByteArray(),
                        dataString.toByteArray().size,
                        InetAddress.getByName("230.0.0.0"),
                        multiCastPort.toInt()
                    )

                    socketMultiConnect.send(datagramPacket)

                    //Set own port and Add own port to list of operators
                    portsAudio.add(portAudio.toString())
                    self.OperatorPort = portAudio.toString()
                    allocatePort(hostAdd.toString(), portAudio.toString())
                    selfAdded = true
                    self.isActive = true // Will always be true
//                            notifyMe()
                }
            } else if (opDetected && selfAdded && !timeOutOp) {
                /** Send all operator information over server
                 * This is used until all operators leave the server
                 */
                val dataString =
                    "OP REQUEST: IP: $hostAdd PORT_AUDIO: $portAudio PORTS_CONNECTED: $portsAudio"
                val datagramPacket = DatagramPacket(
                    dataString.toByteArray(),
                    dataString.toByteArray().size,
                    InetAddress.getByName("230.0.0.0"),
                    multiCastPort.toInt()
                )
                socketMultiConnect.send(datagramPacket)
                if (!self.isActive) {
                    self.isActive = true
                }
                opDetected = false
                timeOutOp = false
            }
        }
    }
}