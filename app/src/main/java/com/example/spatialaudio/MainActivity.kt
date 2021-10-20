package com.example.spatialaudio

import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.net.*
import com.example.spatialaudio.threading.*
import com.example.spatialaudio.dataClass.*
import com.example.spatialaudio.functions.initializeSelf.self as self
import com.example.spatialaudio.variables.*

class MainActivity : AppCompatActivity() {

    //region TextViews
    lateinit var buttonIP: Button
    lateinit var hideButtonIP: Button
    lateinit var textViewIP: TextView
    lateinit var textViewIPConst: TextView
    lateinit var editTextAudio: EditText
    lateinit var IMU: TextView
    lateinit var audioPort: TextView
    lateinit var ENTER_button: TextView
    lateinit var StringRecText: TextView

    lateinit var tester: TextView
    //Troubleshooting TextViews
    lateinit var Op_self            : TextView
    lateinit var Operator           : TextView

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        hostAdd = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        self = self()
        addresses.add(self.OperatorIP)

        Op_self         = findViewById(R.id.Operator_1)
        Operator        = findViewById(R.id.Operator_2)

        var Op1 = troubleshoot(Op = Op_self)
        Op1.Longitude   = findViewById(R.id.Op_1_Long)
        Op1.Latitude    = findViewById(R.id.Op_1_Lat)
        Op1.Nose        = findViewById(R.id.Op_1_Nose)
        Op1.IP          = findViewById(R.id.Op_1_IP)
        Op1.Port        = findViewById(R.id.Op_1_Port)
        Op1.Name        = findViewById(R.id.Op_1_Name)

        var Op2 = troubleshoot(Op = Operator)
        Op2.Longitude   = findViewById(R.id.Op_2_Long)
        Op2.Latitude    = findViewById(R.id.Op_2_Lat)
        Op2.Nose        = findViewById(R.id.Op_2_Nose)
        Op2.IP          = findViewById(R.id.Op_2_IP)
        Op2.Port        = findViewById(R.id.Op_2_Port)
        Op2.Name      = findViewById(R.id.Op_2_Name)

        buttonIP = findViewById(R.id.IP_button)                 //IP Address
        hideButtonIP = findViewById(R.id.hide_IP_button)

        textViewIP = findViewById(R.id.IP_address_textView)     //IP Address Textview
        textViewIPConst = findViewById(R.id.IP_textView)

        editTextAudio = findViewById(R.id.audio_Port_editText)
        ENTER_button = findViewById(R.id.audio_Port_button)
        IMU = findViewById(R.id.IMU_text)
        audioPort = findViewById(R.id.audio_port_textView)

        StringRecText = findViewById(R.id.recText)

        buttonIP.setOnClickListener { revealIP() }
        hideButtonIP.setOnClickListener { hideIP(it) }
        ENTER_button.setOnClickListener{ joinMultiCast() }

        troubleshooting(Op1, Op2, self)
    }

    private fun updateTextView_MYDATA() {
        class myData : Thread() {
            override fun run() {
                while (true) {
                    sleep(updateRate) //sleep 200 milliseconds
                    runOnUiThread {
                        IMU.text =
                            "${myData[0]} ${myData[1]} ${myData[2]}"
                    }
                }
            }
        }
        val thread = Thread(myData())
        thread.start()
    }

//    private fun updateTextView_OPDATA() {
//        class updataOPDATA: Thread() {
//            override fun run() {
//                while (true) {
//                    sleep(100)
//                    runOnUiThread()
//                    {
//                        IMU.text = opDataDataString
//                    }
//                }
//            }
//        }
//        val thread = Thread(updataOPDATA())
//        thread.start()
//    }

    private fun updateTextView_RECMESSAGE() {
        class updateRECMESSAGE: Thread() {
            var holdDataString = ""

            override fun run() {
                while (true) {
                    sleep(1000)
                    runOnUiThread {
                        if(holdDataString != receiverDataString) {
                            StringRecText.text = receiverDataString
                            holdDataString = receiverDataString
                        } else {
                            StringRecText.text = "Awaiting new message"
                        }
                    }
                }
            }
        }
        val thread = Thread(updateRECMESSAGE())
        thread.start()
    }

    private fun joinMultiCast(){
        val ReceiveOPDATA = Thread(ReceiveOperatorData())
        val SendMYDATA = Thread(SendMyData())
        val ReceiveREQUEST = Thread(ReceiveRequest())
        val SendREQUEST = Thread(SendRequest())

        multiCastPort = editTextAudio.text.toString()
        audioPort.text = multiCastPort.toString()
        socketMultiConnect = MulticastSocket(multiCastPort.toInt())
        socketMultiConnect.joinGroup(InetSocketAddress("230.0.0.0", multiCastPort.toInt()), null)

        if(multiCastPort.toInt() > 0) {
            updateTextView_MYDATA()
//          updateTextView_OPDATA()
            updateTextView_RECMESSAGE()

            SendMYDATA.start()
            ReceiveOPDATA.start()
            ReceiveREQUEST.start()
            SendREQUEST.start()
        }
    }

    private fun revealIP(){
        textViewIP.text = self.OperatorIP
    }

    private fun hideIP(view: View){
        textViewIP.text = R.string.hidden.toString()
    }

    private fun troubleshooting(_op1: troubleshoot, _op2: troubleshoot, _self: opInfo) {
        class troubleshoot : Thread() {
            override fun run() {
                while (true) {
                    sleep(100)
                    runOnUiThread {
//                    _op1.Op.text = "Self"
//                    _op1.Longitude.text = operators["OP2"]?.OperatorLongitude.toString()
//                    _op1.Latitude.text = operators["OP2"]?.OperatorLatitude.toString()
//                    _op1.Nose.text = operators["OP2"]?.OperatorNose.toString()
//                    _op1.IP.text = operators["OP2"]?.OperatorIP.toString()
//                    _op1.Port.text = operators["OP2"]?.OperatorPort.toString()
//                        tester.text = operators.toList().toString()
//                    _op1.Name.text = portsAudio.toString()
                        for (i in potentialOP.indices) {
                            if (operators[potentialOP[i]]?.OperatorIP == _self.OperatorIP) {
                                _op1.Op.text = "Self"
                                _op1.Longitude.text = operators[potentialOP[i]]?.OperatorLongitude.toString()
                                _op1.Latitude.text = operators[potentialOP[i]]?.OperatorLatitude.toString()
                                _op1.Nose.text = operators[potentialOP[i]]?.OperatorNose.toString()
                                _op1.IP.text = operators[potentialOP[i]]?.OperatorIP
                                _op1.Port.text = _self.activeTime.toString()
                                _op1.Name.text = _self.offset.toString()
                            } else if (operators[potentialOP[i]]?.OperatorIP != _self.OperatorIP && operators[potentialOP[i]] != null) {
                                _op2.Op.text = "Operator 2"
                                _op2.Longitude.text = operators[potentialOP[i]]?.OperatorLongitude.toString()
                                _op2.Latitude.text = operators[potentialOP[i]]?.OperatorLatitude.toString()
                                _op2.Nose.text = operators[potentialOP[i]]?.OperatorNose.toString()
                                _op2.IP.text = operators[potentialOP[i]]?.OperatorIP
                                _op2.Port.text = operators[potentialOP[i]]?.activeTime.toString()
                                _op2.Name.text = operators[potentialOP[i]]?.offset.toString()
                            }
                        }
                    }
                }
            }
        }
        val thread = Thread(troubleshoot())
        thread.start()
    }
}
