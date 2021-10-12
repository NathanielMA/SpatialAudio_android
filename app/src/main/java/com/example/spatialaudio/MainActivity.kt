package com.example.spatialaudio

//This is a test to verify GitHub access

import android.annotation.SuppressLint
import android.graphics.Path
import android.media.AudioFormat
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.*
import java.util.*
import kotlin.ConcurrentModificationException
import kotlin.concurrent.timerTask
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates
import com.example.spatialaudio.functions.SpatialFun as s

/**Data class that stores operator information
 * Stores:
 *      IP
 *      Port
 *      Host Name
 *      Longitude, Latitude, Distance Azimuth and Nose
 * Note:
 *      offset, activeTime, isActive are used for dynamic port removal
 */
data class opInfo(var OperatorIP: String, var OperatorPort: String = "") {
    var OperatorLongitude: Double = 0.0
    var OperatorLatitude: Double = 0.0
    var OperatorNose: Double = 0.0
    var OperatorAzimuth: Double = 0.0
    var OperatorDistance: Double = 0.0
    var offset: Int = 0
    var activeTime: Int = 0
    var isActive: Boolean = false
}

data class troubleshoot(var Op: TextView){
    lateinit var Longitude: TextView
    lateinit var Latitude: TextView
    lateinit var Nose: TextView
    lateinit var IP: TextView
    lateinit var Port: TextView
    lateinit var active: TextView
}

//region DEMO VARIABLES
private var directionDemo: Int = 0
var DEMO: Boolean = false
//endregion

//region PUBLIC VARIABLES
/**
 * List of connected ports
 */
val portsAudio = mutableSetOf<String>()

/**
 * List of operators connected to server
 */
var operators = mutableMapOf<String, opInfo>()

/**
 * List of all potential operators to be contained in data base.
 */
val potentialOP = listOf<String>("OP1","OP2","OP3","OP4","OP5","OP6","OP7","OP8")

/**
 * Int variable for storing the designated Hpper IMU port.
 */
var IMUPort: Int = 0

/**
 * Detects whether self has been notified for being unable to receive Hyper IMU data.
 */
var notified: Boolean = false

/**
 * This String is used to display information for connecting Hyper IMU if it is not running
 */
lateinit var infoString: String
//endregion

//region PRIVATE VARIABLES
/**
 * TARGETDATALINE: targets primary microphone on device for audio recording.
 */
//private lateinit var mic: TargetDataLine

/**
 * Hyper IMU socket
 */
private var IMUSocket: DatagramSocket = DatagramSocket(9001)

/**
 *  Multicast Socket on port 8010
 */
lateinit var socketMultiConnect: MulticastSocket

/**
 * Data variable which handles TargetDataLine data
 */
private lateinit var data: ByteArray

/**
 * Used alongside the comparator variable to detect when new operators join or leave the server.
 */
private var currentOps = mutableMapOf<String, opInfo>()

/**
 * Used alongside the currentOps variable to detect when new operates join or leave the server.
 */
private var comparator: Int = 0

/**
 * Detect whether the specified button has been pressed/released
 */
private var voice_Chat = 0

/**
 * Buffer size in Bytes for storing audio data
 */
private const val buffer = 1024

/**
 * DatagramSocket used for sending audio data over multicast network.
 */
private val socketSendAudio = DatagramSocket()

/**
 * Audio port of self. Ranging from set Port -> Port + 7.
 */
var portAudio = 7777

/**
 * Placeholder for delegating operator ports for sending audio
 */
private var port by Delegates.notNull<Int>()

/**
 * List of IP's
 */
val addresses = mutableSetOf<String>()

/**
 * Detects new connection request
 */
var opDetected = false

/**
 * Determines whether server has been initialized.
 * If server has not been initialized, this tells the user that they are
 * the first to initialize the server.
 */
var timeOutOp = false

/**
 * Determines if self has been initialized.
 */
var selfAdded = false

/**
 * Determines if operator is already contained within data base.
 */
private var opNotFound = false

/**
 * Mutable list which is used for storing GPS data until it is saved within a data class.
 */
private var opGPS = mutableListOf<String>("","","","","")

/**
 * Determines whether the thread which sends off own audio has been suspended
 */
private var suspended: Boolean = false

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
 * Int variable for incrementing Port number within Functions
 */
private var incPort: Int = portAudio

/**
 * Stores data received from the TARGETDATALINE
 */
private var numBytesRead: Int = 0

/**
 * IP of own device
 */
var hostAdd: String = ""
//endregion

/**
 * This FUNCTION asks the user to set the initial port on which audio should be received and bases
 * all other operators off of initial port.
 *
 * It also sets the Hyper IMU port required for receiving Hyper IMU data
 */

var azimuthData = arrayOf<String>("","","","","","")
var Longitude: Double = 0.0
var Latitude: Double = 0.0
var Nose: Double = 0.0
var dataString: String = ""
var dataString2: String = ""
val portString = 8000
val socketMulti = 8010
var tcanc = 0

class MainActivity : AppCompatActivity() {
    lateinit var buttonIP: Button
    lateinit var hideButtonIP: Button
    lateinit var textViewIP: TextView
    lateinit var textViewIPConst: TextView
    lateinit var editTextAudio: EditText
    lateinit var IMU: TextView
    lateinit var audioPort: TextView
    lateinit var ENTER_button: TextView
    lateinit var StringRecText: TextView

    lateinit var Op_self            : TextView
    lateinit var Operator           : TextView
    lateinit var self_Longitude     : TextView
    lateinit var self_Latitude      : TextView
    lateinit var self_Nose          : TextView
    lateinit var self_Port          : TextView
    lateinit var self_active        : TextView
    lateinit var self_IP            : TextView
    lateinit var Op_Longitude       : TextView
    lateinit var Op_Latitude        : TextView
    lateinit var Op_Nose            : TextView
    lateinit var Op_Port            : TextView
    lateinit var Op_IP              : TextView
    lateinit var Op_active          : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        hostAdd = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

        val self = opInfo (OperatorIP = hostAdd.toString())

        socketMultiConnect = MulticastSocket(socketMulti)
        val stringSocket = DatagramSocket(portString)
        socketMultiConnect.joinGroup(InetSocketAddress("230.0.0.0", 8000), null)

        Op_self         = findViewById(R.id.Operator_1)
        Operator        = findViewById(R.id.Operator_2)

        var Op1 = troubleshoot(Op = Op_self)
        Op1.Longitude   = findViewById(R.id.Op_1_Long)
        Op1.Latitude    = findViewById(R.id.Op_1_Lat)
        Op1.Nose        = findViewById(R.id.Op_1_Nose)
        Op1.IP          = findViewById(R.id.Op_1_IP)
        Op1.Port        = findViewById(R.id.Op_1_Port)
        Op1.active      = findViewById(R.id.Op_1_active)

        var Op2 = troubleshoot(Op = Operator)
        Op2.Longitude   = findViewById(R.id.Op_2_Long)
        Op2.Latitude    = findViewById(R.id.Op_2_Lat)
        Op2.Nose        = findViewById(R.id.Op_2_Nose)
        Op2.IP          = findViewById(R.id.Op_2_IP)
        Op2.Port        = findViewById(R.id.Op_2_Port)
        Op2.active      = findViewById(R.id.Op_2_active)

        buttonIP = findViewById(R.id.IP_button)                 //IP Address
        hideButtonIP = findViewById(R.id.hide_IP_button)

        textViewIP = findViewById(R.id.IP_address_textView)     //IP Address Textview
        textViewIPConst = findViewById(R.id.IP_textView)

        editTextAudio = findViewById(R.id.audio_Port_editText)
        ENTER_button = findViewById(R.id.audio_Port_button)
        IMU = findViewById(R.id.IMU_text)
        audioPort = findViewById(R.id.audio_port_textView)

        StringRecText = findViewById(R.id.recText)

        buttonIP.setOnClickListener { revealIP(self) }
        hideButtonIP.setOnClickListener { hideIP(it) }
        ENTER_button.setOnClickListener{ setAudioPort(it) }

        ConnectThread(self, socketMulti.toString())
        SendStringThread(self, portString, stringSocket)
        ConnectRecThread(self)
        troubleShoot(self, Op1, Op2)
    }

    //Generic set-up for threads
//    private fun thread() {
//        class changeme : Thread() {
//            override fun run() {
//                while (true) {
//                    TODO()
//                }
//            }
//        }
//        fun changeMe(){
//            TODO()
//        }
//        val thread = Thread(changeme())
//        thread.start()
//    }


    //sendRequest
    /**
     * setup statements to prevent the sending of strings without a proper port set
     */
    private fun ConnectThread(_self: opInfo, portConnect: String) {
        class _connectTread : Thread() {
            override fun run() {
                while (true) {
                    // Initialize first operator (self) on server
                    sleep(1000)
                    if(timeOutOp) {
                        sleep(1000)
                        if (addresses.isNullOrEmpty()) {
                            /** Send own information over server
                             * This is used until at least one operator joins
                             */
                            val dataString = "OP REQUEST: IP: $hostAdd PORT_AUDIO: $portAudio"

                            val datagramPacket = DatagramPacket(
                                dataString.toByteArray(),
                                dataString.toByteArray().size,
                                InetAddress.getByName("230.0.0.0"),
                                portConnect.toInt()
                            )

                            socketMultiConnect.send(datagramPacket)

                            //Set own port and Add own port to list of operators
                            portsAudio.add(portAudio.toString())
                            _self.OperatorPort = portAudio.toString()
                            allocatePort(hostAdd.toString(), portAudio.toString())
                            selfAdded = true
                            _self.isActive = true // Will always be true
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
                            portConnect.toInt()
                        )
                        socketMultiConnect.send(datagramPacket)
                        if(!_self.isActive){
                            _self.isActive = true
                        }
                        opDetected = false
                        timeOutOp = false
                    }
                }
            }

            fun allocatePort(IP: String, Port: String){
                for(i in potentialOP.indices) {
                    when (Port.toInt()) {
                        incPort + i -> {
                            operators[potentialOP[i]] = opInfo(OperatorIP = IP)
                            operators[potentialOP[i]]?.OperatorPort = Port
                        }
                    }
                }
            }
        }

        val thread = Thread(_connectTread())
        thread.start()
    }

    //receiveOP
    private fun ConnectRecThread(_self: opInfo) {
        class sampleRec : Thread() {
            override fun run() {
                val t = Timer()
                while (true) {
                    if (!opDetected && !selfAdded || selfAdded && !timeOutOp) {
                        sleep(100)
                        t.schedule(timerTask {
                            timeOutOp = true
                        }, 5000)
                    }

                    val buffer = ByteArray(1024)
                    val response = DatagramPacket(buffer, 1024)

                    socketMultiConnect.receive(response)

                    val data = response.data
                    val dataString = String(data, 0, data.size)

                    val sample = arrayOf<String>("", "", "", "")

                    when {
                        """OP REQUEST: """.toRegex()
                            .containsMatchIn(dataString) -> {

                            if (!selfAdded && tcanc == 0) {
                                t.cancel()
                                t.purge()
                                tcanc = 1
                            }

                            /* Variables used to store and recognize parsed data from received packets
                             * Variables will Regex:
                             *      operator IP, Name, Port and total Ports on server
                             */
                            val opIP =
                                """(\d+)\.(\d+)\.(\d+)\.(\d+)""".toRegex().find(dataString)?.value
                            val opPort = """(?<=PORT_AUDIO: )\d*""".toRegex()
                                .find(dataString)?.value.toString()
                            val opPortR = """\d\d\d\d""".toRegex()
                            val patt = opPortR.findAll(dataString)

                            if (!addresses.contains(opIP)) { // New operator detected
                                try {
                                    if (opIP != _self.OperatorIP) {  // New operator is not self
                                        var i = 0
                                        updateTextView(dataString)
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
                                        addresses.add(opIP.toString())                  // Add IP to addresses set
//                                        notifyMe()
                                    }

                                    /* Determine whether to take initial Port
                                     * Will only be used if port initial Port has left server and removed from portsAudio set
                                     */
                                    if (!portsAudio.contains(portAudio.toString()) && !selfAdded) {
                                        portsAudio.add(portAudio.toString())
                                        _self.OperatorPort = portAudio.toString()
                                        allocatePort(
                                            hostAdd.toString(),
                                            portAudio.toString()

                                        )
                                        selfAdded = true
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
                                val portsInUse = portsAudio.toList()
                                if(!selfAdded) {
                                    Thread.sleep(100)
                                    Timer().schedule(timerTask {
                                        if (portsAudio.contains(portAudio.toString()) && !selfAdded) {
                                            for (i in 0 until portsAudio.size) {
                                                if (portsAudio.contains(portAudio.toString())) {
                                                    portAudio += 1
                                                } else if ((portAudio - incPort) >= 8){
                                                    break
                                                }
                                            }
                                            portsAudio.add(portAudio.toString())
                                            _self.OperatorPort = portAudio.toString()
                                            allocatePort(hostAdd.toString(), portAudio.toString())
                                            selfAdded = true
                                        }
                                    }, (1000..3000).random().toLong())
                                }else if (operators.size < portsAudio.size && selfAdded){
                                    for(i in 0 until portsAudio.size) {
                                        removePort(portsInUse[i])
                                    }
                                }
                            }
                            opDetected = true
                            timeOutOp = false
                        }
                    }
                }
            }

            fun allocatePort(IP: String, Port: String){
                for(i in potentialOP.indices) {
                    when (Port.toInt()) {
                        incPort + i -> {
                            operators[potentialOP[i]] = opInfo(OperatorIP = IP)
                            operators[potentialOP[i]]?.OperatorPort = Port
                        }
                    }
                }
            }

            /**
             * PRIVATE: This FUNCTION will remove an operator and disassociate them from their port if their
             * connection is interrupted and disconnect.
             */
            private fun removePort(Port: String){
                for (i in potentialOP.indices) {
                    when (Port.toInt()) {
                        incPort + i -> {
                            if (!operators.containsKey(potentialOP[i])) {
                                portsAudio.remove(Port)
                            }
                        }
                    }
                }
            }

            fun updateTextView(String: String) {
                runOnUiThread {
                    StringRecText.text = "$String"
                }
            }
        }
            val thread = Thread(sampleRec())
            thread.start()
    }

    //sendData
    private fun SendStringThread(_self: opInfo, portString: Int, stringSocket: DatagramSocket) {

        fun getData(_self: opInfo, IMUSocket: DatagramSocket): List<Double> {
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
                    _self.OperatorLongitude = Longitude
                    _self.OperatorLatitude = Latitude
                    _self.OperatorNose = Nose
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

        fun updateTextView() {
            runOnUiThread {
                IMU.text = "${_self.OperatorLongitude} ${_self.OperatorLatitude} ${_self.OperatorNose}"
            }
        }

        class updateFast : Thread(){
            override fun run() {
                while (true) {
                    sleep(200)
                    getData(_self, IMUSocket)
                    updateTextView()
                }
            }
        }

        class sendData : Thread() {
            override fun run() {
                while (true) {
                    sleep(2000)

                    val myData = getData(_self, IMUSocket)
                    _self.activeTime += 1

                    val time = Date().toString()
                    val messageTo = "IP-$hostAdd PORT_AUDIO: $portAudio COORDS: $myData--"
                    val mes1 = (messageTo + time).toByteArray()

                    for(i in 0 until addresses.size){
                        val request = DatagramPacket(
                            mes1,
                            mes1.size,
                            Inet4Address.getByName(addresses.elementAtOrNull(i)),
                            portString.toInt())
                        stringSocket.send(request)
                    }

                    try{
                        for (key in operators.keys) {
                            if ((_self.activeTime - operators[key]?.activeTime!!) - operators[key]?.offset!! > 1 && operators[key]?.OperatorIP != _self.OperatorIP) {
                                operators[key]!!.isActive = false
                                portsAudio.remove(operators[key]!!.OperatorPort)
                                addresses.remove(operators[key]?.OperatorIP)
                                operators.remove(key)
                            }
                        }
                    } catch (e: ConcurrentModificationException){
                        TODO()
                    }
                }
            }
        }

        val thread = Thread(sendData())
        val thread2 = Thread(updateFast())
        thread.start()
        thread2.start()
    }

    //receiveData
    private fun RecStringThread(stringSocket: DatagramSocket, _self: opInfo) {
        class changeme : Thread() {
            override fun run() {
                while (true) {
                    val buffer2 = ByteArray(1024)
                    val response2 = DatagramPacket(buffer2, 1024)

                    stringSocket.receive(response2)

                    val data2 = response2.data
                    val dataString = String(data2, 0, data2.size)

                    /** Variables used to store and recognize parsed data from received packets
                     * Variables will Regex:
                     *      operator IP, Name, Port and Coordinates
                     */
                    val opName = """(?<=OP-)\w+""".toRegex().find(dataString)?.value.toString()
                    val opIP =
                        """(?<=IP: )(\d+).(\d+).(\d+).(\d+)""".toRegex().find(dataString)?.value.toString()
                    val opPort = """(?<=PORT_AUDIO: )\d+""".toRegex().find(dataString)?.value.toString()
                    val opCoords = """-?(\d+)\.\d+""".toRegex()
                    val patt = opCoords.findAll(dataString)

                    var i = 0
                    patt.forEach { f ->
                        opGPS[i] = f.value
                        i++
                    }

                    //Allocate received coordinates to correct operator
                    allocateCoords(opPort)

                    for (key in operators.keys) {
                        if (operators[key]?.OperatorIP != hostAdd) {

                            // Calculate Azimuth between self and operator
                            operators[key]?.OperatorAzimuth = AzimuthCalc(
                                _self.OperatorLongitude,
                                _self.OperatorLatitude,
                                operators[key]!!.OperatorLongitude,
                                operators[key]!!.OperatorLatitude,
                                _self.OperatorNose
                            )

                            //Calculate distance between self and operator
                            operators[key]?.OperatorDistance = OperatorDistance(
                                _self.OperatorLongitude,
                                _self.OperatorLatitude,
                                operators[key]!!.OperatorLongitude,
                                operators[key]!!.OperatorLatitude
                            )
                    }

                    if (!portsAudio.contains(opPort)) {
                        if(!opDetected && !operators.containsKey(opPort)){
                            Timer().schedule(timerTask {
                                opNotFound = true
                            }, 5000)
                        }
                    }
                    if (opNotFound){
                        portsAudio.add(opPort)
                        addresses.add(opIP)
                        allocatePort(opIP, opPort)
                        opNotFound = false
                    }

                    operatorTimeOut(opIP)
                }
                }
            }

            /**
             * PRIVATE: This FUNCTION will utilize the GPS data held in the operators DATA CLASS to calculate their distance. The
             * distance is relative to self.
             */
            private fun OperatorDistance(myLongitude: Double, myLatitude: Double, opLongitude: Double, opLatitude: Double): Double{
                val dLat: Double = Math.toRadians(opLatitude - myLatitude)
                val dLong: Double = Math.toRadians(opLongitude - myLongitude)
                val a = Math.pow(Math.sin(dLat/2), 2.0) + Math.cos(myLatitude) * Math.cos(opLatitude) * Math.pow(Math.sin(dLong/2), 2.0)
                val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
                val R = 6300000
                val feet = ((R * c) * 100)/(2.54 * 12)

                return (feet)
            }

            /**
             * PRIVATE: This FUNCTION will utilize the GPS data held in the operators DATA CLASS to calculate their azimuth.
             * The azimuth is taken with respect to self and altered based on direction self is facing.
             */
            private fun AzimuthCalc(myLongitude: Double, myLatitude: Double, opLongitude: Double, opLatitude: Double, Nose: Double): Double {
                val endLongitude: Double = Math.toRadians(opLongitude - myLongitude)
                val endLatitude: Double = Math.toRadians(opLatitude)
                val startLatitude = Math.toRadians(myLatitude)
                var phi = Math.toDegrees(atan2(sin(endLongitude) * cos(endLatitude), cos(startLatitude) * sin(endLatitude) - sin(startLatitude) * cos(endLatitude) * cos(endLongitude)))

                (phi + 360) % 360

                if(Nose < phi){
                    phi = phi - Nose
                } else if(Nose > phi && ((Nose - phi) < 180)){
                    phi = Nose - phi
                } else if (Nose > phi && ((Nose - phi) > 180)){
                    phi = 360 - (Nose - phi)
                }

                return(phi)
            }

            /**
             * PRIVATE: This FUNCTION will assign each operator their unique Longitude and Latitude data
             * based upon their coordinates sent via Hyper IMU.
             */
            private fun allocateCoords(Port: String) {
                for (i in potentialOP.indices){
                    when (Port.toInt()) {
                        incPort + i -> {
                            operators[potentialOP[i]]?.OperatorLongitude = opGPS[2].toDouble()
                            operators[potentialOP[i]]?.OperatorLatitude = opGPS[3].toDouble()
                        }
                    }
                }
            }

            fun allocatePort(IP: String, Port: String){
                for(i in potentialOP.indices) {
                    when (Port.toInt()) {
                        incPort + i -> {
                            operators[potentialOP[i]] = opInfo(OperatorIP = IP)
                            operators[potentialOP[i]]?.OperatorPort = Port
                        }
                    }
                }
            }

            /**
             * PRIVATE: This FUNCTION detects if an operator has left the Multicast network.
             */
            private fun operatorTimeOut(IP: String) {
                for(key in operators.keys){
                    if (operators[key]!!.OperatorIP == IP) {
                        if(!operators[key]!!.isActive) {
                            operators[key]?.isActive = true
                        }
                        operators[key]!!.activeTime += 1

                        operators[key]!!.offset = _self.activeTime - operators[key]!!.activeTime
                    }
                }
            }
        }
        val thread = Thread(changeme())
        thread.start()
    }

    //sendAudio
    private fun SendThread() {
        class changeme : Thread() {
            override fun run() {
                while (true) {
                    TODO()
                }
            }
            fun changeMe(){
                TODO()
            }
        }
        val thread = Thread(changeme())
        thread.start()
    }

    private fun troubleShoot(_self: opInfo, op1: troubleshoot, op2: troubleshoot) {
        class _troubleShoot : Thread() {

            fun updateTextView() {
                runOnUiThread {
                    for (ops in potentialOP.indices) {
                        if(operators[potentialOP[ops]]?.OperatorIP == _self.OperatorIP) {
                            op1.Op.text = "Self"
                            op1.Longitude.text = _self.OperatorLongitude.toString()
                            op1.Latitude.text = _self.OperatorLatitude.toString()
                            op1.Nose.text = _self.OperatorNose.toString()
                            op1.IP.text = _self.OperatorIP
                            op1.Port.text = _self.OperatorPort
                            op1.active.text = _self.isActive.toString()
                        } else {
                            op2.Op.text = "Operator 2"
                            op2.Longitude.text = operators[potentialOP[ops]]?.OperatorLongitude.toString()
                            op2.Latitude.text = operators[potentialOP[ops]]?.OperatorLatitude.toString()
                            op2.Nose.text = operators[potentialOP[ops]]?.OperatorNose.toString()
                            op2.IP.text = operators[potentialOP[ops]]?.OperatorIP
                            op2.Port.text = operators[potentialOP[ops]]?.OperatorPort
                            op2.active.text = operators[potentialOP[ops]]?.isActive.toString()
                        }
                    }
                }
            }

            override fun run() {
                while (true) {
                    sleep(200)
                    updateTextView()
                }
            }
        }

        val thread = Thread(_troubleShoot())
        thread.start()
    }

    private fun revealIP(_self: opInfo){
        textViewIP.text = _self.OperatorIP
    }

    private fun hideIP(view: View){
        textViewIP.text = R.string.hidden.toString()
    }

    private fun setAudioPort(view: View){
        audioPort.text = editTextAudio.text
    }

}
