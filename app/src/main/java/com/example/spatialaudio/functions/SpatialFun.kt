package com.example.spatialaudio.functions

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.example.spatialaudio.*
import org.w3c.dom.Text
import java.io.IOException
import java.net.*
import java.util.*

@SuppressLint("StaticFieldLeak")
object SpatialFun{
//    lateinit var _self: opInfo
//    private var timer = Timer()
//
//    fun getSelf(self: opInfo){
//        _self = self
//    }
//
//    fun connecter(portConnect: String) {
//        while (true) {
//            // Initialize first operator (self) on server
//            Thread.sleep(1000)
//            if(timeOutOp) {
//                Thread.sleep(1000)
//                if (addresses.isNullOrEmpty()) {
//                    /** Send own information over server
//                     * This is used until at least one operator joins
//                     */
//                    val dataString = "OP REQUEST: IP: $hostAdd PORT_AUDIO: $portAudio"
//
//                    val datagramPacket = DatagramPacket(
//                        dataString.toByteArray(),
//                        dataString.toByteArray().size,
//                        InetAddress.getByName("230.0.0.0"),
//                        portConnect.toInt()
//                    )
//
//                    socketMultiConnect.send(datagramPacket)
//
//                    //Set own port and Add own port to list of operators
//                    portsAudio.add(portAudio.toString())
//                    _self.OperatorPort = portAudio.toString()
//                    allocatePort(hostAdd.toString(), portAudio.toString())
//                    selfAdded = true
//                    _self.isActive = true // Will always be true
////                            notifyMe()
//                }
//            } else if (opDetected && selfAdded && !timeOutOp) {
//                /** Send all operator information over server
//                 * This is used until all operators leave the server
//                 */
//                val dataString =
//                    "OP REQUEST: IP: $hostAdd PORT_AUDIO: $portAudio PORTS_CONNECTED: $portsAudio"
//                val datagramPacket = DatagramPacket(
//                    dataString.toByteArray(),
//                    dataString.toByteArray().size,
//                    InetAddress.getByName("230.0.0.0"),
//                    portConnect.toInt()
//                )
//                socketMultiConnect.send(datagramPacket)
//                if(!_self.isActive){
//                    _self.isActive = true
//                }
//                opDetected = false
//                timeOutOp = false
//            }
//        }
//    }
//
//    fun connectRec(view: TextView, mainActivity: MainActivity) {
//        if (!opDetected && !selfAdded || selfAdded && !timeOutOp) {
//            Thread.sleep(100)
//            AsynchTaskTimer()
//        }
//
//        val buffer = ByteArray(1024)
//        val response = DatagramPacket(buffer, 1024)
//
//        socketMultiConnect.receive(response)
//
//        val data = response.data
//        val dataString = String(data, 0, data.size)
//
//        val sample = arrayOf<String>("", "", "", "")
//
//        when {
//            """OP REQUEST: """.toRegex()
//                .containsMatchIn(dataString) -> {
//
//                if (!selfAdded && tcanc == 0) {
//                    timer.cancel()
//                    timer.purge()
//                    tcanc = 1
//                }
//
//                /* Variables used to store and recognize parsed data from received packets
//                 * Variables will Regex:
//                 *      operator IP, Name, Port and total Ports on server
//                 */
//                val opIP =
//                    """(\d+)\.(\d+)\.(\d+)\.(\d+)""".toRegex().find(dataString)?.value
//                val opPort = """(?<=PORT_AUDIO: )\d*""".toRegex()
//                    .find(dataString)?.value.toString()
//                val opPortR = """\d\d\d\d""".toRegex()
//                val patt = opPortR.findAll(dataString)
//
//                if (!addresses.contains(opIP)) { // New operator detected
//                    try {
//                        if (opIP != _self.OperatorIP) {  // New operator is not self
//                            var i = 0
//                            updateTextView(view, mainActivity, dataString)
//                            /* Sort through all Ports found
//                             * Add all ports to portsAudio set
//                             */
//                            patt.forEach { f ->
//                                sample[i] = f.value
//                                if (sample[i] != "") {
//                                    portsAudio.add(sample[i])
//                                    i++
//                                }
//                            }
//
//                            allocatePort(
//                                opIP.toString(),
//                                opPort
//                            )   // Set operator information
//                            addresses.add(opIP.toString())                  // Add IP to addresses set
////                                        notifyMe()
//                        }
//
//                        /* Determine whether to take initial Port
//                         * Will only be used if port initial Port has left server and removed from portsAudio set
//                         */
//                        if (!portsAudio.contains(portAudio.toString()) && !selfAdded) {
//                            portsAudio.add(portAudio.toString())
//                            _self.OperatorPort = portAudio.toString()
//                            allocatePort(
//                                hostAdd.toString(),
//                                portAudio.toString()
//
//                            )
//                            selfAdded = true
//                        }
//
//                    } catch (e: BindException) { // Catch a Bind exception if portAudio is already bound
//
//                    }
//                    /** Dynamically set port
//                     * In order of statements:
//                     *      First:
//                     *          Compare own port, starting at initial Port, to received ports.
//                     *          If port exists, own port is increased.
//                     *          Repeats until own port does not exist within set.
//                     *          Will not exceed 8.
//                     *
//                     *          Note: Self will be added within a random interval between 1 - 4 seconds.
//                     *          This is to ensure the correct allocation for each operator if they happen
//                     *          to join the server at the same moment.
//                     *      Second:
//                     *          If there exists more ports than operators on server.
//                     *          Compare existing ports to current operators.
//                     *          Remove extra port.
//                     */
//                    val portsInUse = portsAudio.toList()
//                    if(!selfAdded) {
//
//                        Thread.sleep(100)
//                        AsynchRandomTaskTimer()
//
//                    }else if (operators.size < portsAudio.size && selfAdded){
//                        for(i in 0 until portsAudio.size) {
//                            removePort(portsInUse[i])
//                        }
//                    }
//                }
//                opDetected = true
//                timeOutOp = false
//            }
//        }
//    }
//
//    /**
//     * PRIVATE: This FUNCTION will remove an operator and disassociate them from their port if their
//     * connection is interrupted and disconnect.
//     */
//    private fun removePort(Port: String){
//        for (i in potentialOP.indices) {
//            when (Port.toInt()) {
//                incPort + i -> {
//                    if (!operators.containsKey(potentialOP[i])) {
//                        portsAudio.remove(Port)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun AsynchRandomTaskTimer() {
//        val timertask: TimerTask = object : TimerTask() {
//            override fun run() {
//                if (portsAudio.contains(portAudio.toString()) && !selfAdded) {
//                    for (i in 0 until portsAudio.size) {
//                        if (portsAudio.contains(portAudio.toString())) {
//                            portAudio += 1
//                        } else if ((portAudio - incPort) >= 8){
//                            break
//                        }
//                    }
//                    portsAudio.add(portAudio.toString())
//                    _self.OperatorPort = portAudio.toString()
//                    allocatePort(hostAdd.toString(), portAudio.toString())
//                    selfAdded = true
//                }
//            }
//        }
//        timer = Timer()
//        timer.schedule(timertask, (1000..3000).random().toLong())
//    }
//
//    private fun AsynchTaskTimer() {
//        val timertask: TimerTask = object : TimerTask() {
//            override fun run() {
//                timeOutOp = true
//            }
//        }
//        timer = Timer()
//        timer.schedule(timertask, 5000)
//    }
//
//    private fun updateTextView(view: TextView, mainActivity: MainActivity, String: String) {
//        mainActivity.runOnUiThread() {
//            view.text = String
//        }
//    }
//
//    private fun allocatePort(IP: String, Port: String){
//        for(i in potentialOP.indices) {
//            when (Port.toInt()) {
//                incPort + i -> {
//                    operators[potentialOP[i]] = opInfo(OperatorIP = IP)
//                    operators[potentialOP[i]]?.OperatorPort = Port
//                    operators[potentialOP[i]]?.OperatorName = potentialOP[i]
//                }
//            }
//        }
//    }
//
////    fun test(text: TextView, mainActivity: MainActivity) {
////        mainActivity.runOnUiThread{
////            text.text = "Yes"
////        }
////    }

}