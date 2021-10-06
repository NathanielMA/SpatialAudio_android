package com.example.spatialaudio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.*
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
data class opInfo(var OperatorName: String, var OperatorPort: String = "") {
    var OperatorIP: String = ""
    var OperatorLongitude: Double = 0.0
    var OperatorLatitude: Double = 0.0
    var OperatorNose: Double = 0.0
    var OperatorAzimuth: Double = 0.0
    var OperatorDistance: Double = 0.0
    var offset: Int = 0
    var activeTime: Int = 0
    var isActive: Boolean = false
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

lateinit var _self: opInfo
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
 * Port in which strings are sent over
 */
private lateinit var stringSocket: DatagramSocket

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
var portAudio = 0

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
private var incPort: Int = 0

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

class MainActivity : AppCompatActivity() {
    lateinit var buttonIP: Button
    lateinit var hideButtonIP: Button
    lateinit var textViewIP: TextView
    lateinit var textViewIPConst: TextView
    lateinit var editTextAudio: EditText
    lateinit var IMU: TextView
    lateinit var audioPort: TextView
    lateinit var ENTER_button: TextView
    lateinit var recText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        hostAdd = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

        socketMultiConnect = MulticastSocket(8000)
        socketMultiConnect.joinGroup(InetSocketAddress("230.0.0.0", 8000), null)

        buttonIP = findViewById(R.id.IP_button)
        hideButtonIP = findViewById(R.id.hide_IP_button)

        textViewIP = findViewById(R.id.IP_address_textView)
        textViewIPConst = findViewById(R.id.IP_textView)

        editTextAudio = findViewById(R.id.audio_Port_editText)
        ENTER_button = findViewById(R.id.audio_Port_button)
        IMU = findViewById(R.id.IMU_text)
        audioPort = findViewById(R.id.audio_port_textView)

        recText = findViewById(R.id.recText)

        buttonIP.setOnClickListener { revealIP(hostAdd) }
        hideButtonIP.setOnClickListener { hideIP(it) }
        ENTER_button.setOnClickListener{ setAudioPort(it) }

        RecThread()
        AzimuthThread()
        sendThread()
    }

    private fun RecThread() {

        class sampleRec : Thread() {
            override fun run() {
                while(true) {
                    getRec()
                    tester()
                }
            }

            fun getRec() {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)

                socketMultiConnect.receive(packet)
                val data = packet.data
                dataString2 = String(data, 0, data.size)

            }

            fun tester() {
                runOnUiThread{
                    recText.text = dataString2
                }
            }
        }

        val thread = Thread(sampleRec())
        thread.start()
    }

    private fun sendThread() {

        class send : Thread() {
            override fun run() {
                while(true) {
                    sendRequest(audioPort.text.toString())
                }
            }
        }

        val thread = Thread(send())
        thread.start()
    }

    fun AzimuthThread() {
        class azimuthThread: Thread() {
            override fun run() {
                while(true){
                    AzimuthData()
                    updateTextView()
                }
            }

            fun AzimuthData() {
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
//                    _self.OperatorLongitude = Longitude
//                    _self.OperatorLatitude = Latitude
//                    _self.OperatorNose = Nose
                    } catch (e: NumberFormatException) {

                        Longitude = 0.0
                        Latitude = 0.0
                        Nose = 0.0
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("SetTextI18n")
            fun updateTextView() {
                runOnUiThread {
                    IMU.text = "$Longitude $Latitude $Nose"
                }
            }
        }

        val thread = Thread(azimuthThread())
        thread.start()
    }


    private fun revealIP(IP: String){
        textViewIP.text = IP
    }

    private fun hideIP(view: View){
        textViewIP.text = R.string.hidden.toString()
    }

    private fun setAudioPort(view: View){
        audioPort.text = editTextAudio.text
    }

    /**
     * This FUNCTION sends Operator join requests to all operators on MultiCast network.
     */
    fun sendRequest(portConnect: String){
        // Initialize first operator (self) on server
        Thread.sleep(1000)
//        if(timeOutOp) {
//            Thread.sleep(1000)
            if (addresses.isNullOrEmpty()) {
                /** Send own information over server
                 * This is used until at least one operator joins
                 */
                val dataString = "OP REQUEST: IP: $hostAdd " //PORT_AUDIO: $portConnect"

                val datagramPacket = DatagramPacket(dataString.toByteArray(),
                    dataString.toByteArray().size,
                InetAddress.getByName("230.0.0.0"),
                8000)

                socketMultiConnect.send(datagramPacket)

//                //Set own port and Add own port to list of operators
//                portsAudio.add(portAudio.toString())
//                _self.OperatorPort = portAudio.toString()
//
////                allocatePort(hostName.toString(), portAudio.toString(), hostAdd.toString())
//
//                selfAdded = true
//                _self.isActive = true // Will always be true

//                notifyMe()
            }
//        } else if (opDetected && selfAdded && !timeOutOp) {
//            /** Send all operator information over server
//             * This is used until all operators leave the server
//             */
//            val dataString =
//                "OP REQUEST: IP: $hostAdd PORT_AUDIO:" // $portAudio PORTS_CONNECTED: $portsAudio"
//            val datagramPacket = DatagramPacket(
//                dataString.toByteArray(),
//                dataString.toByteArray().size
//            )
//
//            socketMultiConnect.send(datagramPacket)
//
//            if(!_self.isActive){
//                _self.isActive = true
//            }
//            opDetected = false
//            timeOutOp = false
//        }
    }
}