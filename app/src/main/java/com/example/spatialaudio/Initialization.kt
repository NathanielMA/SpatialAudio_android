package com.example.spatialaudio

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spatialaudio.dataClass.opInfo
import com.example.spatialaudio.dataClass.troubleshoot
import com.example.spatialaudio.databinding.InitializationBinding
import com.example.spatialaudio.functions.initializeSelf
import com.example.spatialaudio.threading.ReceiveOperatorData
import com.example.spatialaudio.threading.ReceiveRequest
import com.example.spatialaudio.threading.SendMyData
import com.example.spatialaudio.threading.SendRequest
import com.example.spatialaudio.variables.*
import java.net.InetSocketAddress
import java.net.MulticastSocket

class Initialization : Fragment() {

    private var _binding: InitializationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = InitializationBinding.inflate(inflater, container, false)

        val wifiManager = activity?.applicationContext?.getSystemService(AppCompatActivity.WIFI_SERVICE) as WifiManager
        hostAdd = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)

        self = initializeSelf.self()
        addresses.add(self.OperatorIP)

        binding.IPButton.setOnClickListener { revealIP() }
        binding.hideIPButton.setOnClickListener { hideIP(it) }
        binding.audioPortButton.setOnClickListener{ joinMultiCast() }

        updateTextView_MYDATA()

        binding.audioPortTextView.text = multiCastPort

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val Op1 = troubleshoot(Op = binding.Operator1)
        Op1.Longitude   = binding.Op1Long
        Op1.Latitude    = binding.Op1Lat
        Op1.Nose        = binding.Op1Nose
        Op1.IP          = binding.Op1IP
        Op1.Port        = binding.Op1Port
        Op1.Name        = binding.Op1Name

        val Op2 = troubleshoot(Op = binding.Operator2)
        Op2.Longitude   = binding.Op2Long
        Op2.Latitude    = binding.Op2Lat
        Op2.Nose        = binding.Op2Nose
        Op2.IP          = binding.Op2IP
        Op2.Port        = binding.Op2Port
        Op2.Name        = binding.Op2Name

        troubleshooting(Op1, Op2, self)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    private fun updateTextView_MYDATA() {
        class myData : Thread() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                while (true) {
                    sleep(updateRate) //sleep 200 milliseconds
                    activity?.runOnUiThread {
                        binding.IMUText.text =
                            "${myData[0]} ${myData[1]} ${myData[2]}"
                    }
                }
            }
        }
        val thread = Thread(myData())
        thread.start()
    }

    private fun updateTextView_RECMESSAGE() {
        class updateRECMESSAGE: Thread() {
            var holdDataString = ""

            @SuppressLint("SetTextI18n")
            override fun run() {
                while (true) {
                    sleep(1000)
                    activity?.runOnUiThread {
                        if(holdDataString != receiverDataString) {
                            binding.recText.text = receiverDataString
                            holdDataString = receiverDataString
                        } else {
                            binding.recText.text = "Awaiting new message"
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

        multiCastPort = binding.audioPortEditText.text.toString()
        binding.audioPortTextView.text = multiCastPort
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
        binding.IPAddressTextView.text = self.OperatorIP
    }

    @SuppressLint("SetTextI18n")
    private fun hideIP(view: View){
        binding.IPAddressTextView.text = "hidden"
    }

    private fun troubleshooting(_op1: troubleshoot, _op2: troubleshoot, _self: opInfo) {
        class troubleshoot : Thread() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                while (true) {
                    sleep(100)
                    activity?.runOnUiThread {
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
                                _op1.Port.text = operators[potentialOP[i]]?.OperatorPort
                                _op1.Name.text = operators[potentialOP[i]]?.OperatorName
                            } else if (operators[potentialOP[i]]?.OperatorIP != _self.OperatorIP && operators[potentialOP[i]] != null) {
                                _op2.Op.text = "Operator 2"
                                _op2.Longitude.text = operators[potentialOP[i]]?.OperatorLongitude.toString()
                                _op2.Latitude.text = operators[potentialOP[i]]?.OperatorLatitude.toString()
                                _op2.Nose.text = operators[potentialOP[i]]?.OperatorNose.toString()
                                _op2.IP.text = operators[potentialOP[i]]?.OperatorIP
                                _op2.Port.text = operators[potentialOP[i]]?.OperatorPort
                                _op2.Name.text = operators[potentialOP[i]]?.OperatorName
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