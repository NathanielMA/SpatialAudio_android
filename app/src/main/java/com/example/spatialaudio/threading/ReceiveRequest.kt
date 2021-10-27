package com.example.spatialaudio.threading

import com.example.spatialaudio.functions.NoNewOp.mTimer
import com.example.spatialaudio.functions.AudioPortAssign.assignPort as assign
import com.example.spatialaudio.variables.*
import com.example.spatialaudio.functions.PortAllocation.allocatePort
import com.example.spatialaudio.functions.PortRemoval.removePort
import com.example.spatialaudio.functions.NoNewOp.timeout as timeOut
import com.example.spatialaudio.functions.OperatorTimeOut.operatorTimeOut as OpTimeOut
import java.net.BindException
import java.net.DatagramPacket


class ReceiveRequest : Thread() {
    override fun run() {
        while (true) {
            sleep(200)

            if (!opDetected && !selfAdded || selfAdded && !timeOutOp) {
                sleep(100)
                timeOut()
            }

            val buffer = ByteArray(1024)
            val response = DatagramPacket(buffer, 1024)

            socketMultiConnect.receive(response)

            val data = response.data
            val dataString = String(data, 0, data.size)
            receiverDataString = dataString
            val sample = arrayOf<String>("", "", "", "")

            when {
                """OP REQUEST: """.toRegex()
                    .containsMatchIn(dataString) -> {
                    if(!timeOutOp) {
                        mTimer.cancel()
                    }
                    /* Variables used to store and recognize parsed data from received packets
                     * Variables will Regex:
                     *      operator IP, Name, Port and total Ports on server
                     */
                    val opIP =
                        """(?<=IP: )(\d+)\.(\d+)\.(\d+)\.(\d+)""".toRegex().find(dataString)?.value

                    if (opIP != self.OperatorIP) {
                        val opPort = """(?<=PORT_AUDIO: )\d*""".toRegex()
                            .find(receiverDataString)?.value.toString()
                        val opPortR = """\d\d\d\d""".toRegex()
                        val patt = opPortR.findAll(receiverDataString)

                        if (!addresses.contains(opIP)) { // New operator detected
                            try {
                                if (opIP != self.OperatorIP) {  // New operator is not self
                                    var i = 0
                                    /* Sort through all Ports found
                                 * Add all ports to portsAudio set
                                 */
                                    patt.forEach { f ->
                                        sample[i] = f.value
                                        if (sample[i] != "") {
                                            portsAudio.add(sample[i])
                                            i++
                                        }
                                    }

                                    allocatePort(
                                        opIP.toString(),
                                        opPort
                                    )   // Set operator information
                                    if (!addresses.contains(self.OperatorIP)) {
                                        addresses.add(self.OperatorIP)
                                    }

                                    addresses.add(opIP.toString())                  // Add IP to addresses set
//                                        notifyMe()
                                    OpTimeOut(opIP.toString())
                                }
                            } catch (e: BindException) { // Catch a Bind exception if portAudio is already bound

                            }
                            /** Dynamically set port
                             * In order of statements:
                             *      First:
                             *          Compare own port, starting at initial Port, to received ports.
                             *          If port exists, own port is increased.
                             *          Repeats until own port does not exist within set.
                             *          Will not exceed 8.
                             *
                             *          Note: Self will be added within a random interval between 1 - 4 seconds.
                             *          This is to ensure the correct allocation for each operator if they happen
                             *          to join the server at the same moment.
                             *      Second:
                             *          If there exists more ports than operators on server.
                             *          Compare existing ports to current operators.
                             *          Remove extra port.
                             */
                            assign()
                        }
                        opDetected = true
                        timeOutOp = false
                    }
                }
            }
        }
    }
}