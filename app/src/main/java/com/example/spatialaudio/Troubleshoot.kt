package com.example.spatialaudio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spatialaudio.databinding.TroubleshootBinding
import com.example.spatialaudio.variables.myData
import com.example.spatialaudio.variables.updateRate

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class Troubleshoot : Fragment() {

    private var _binding: TroubleshootBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = TroubleshootBinding.inflate(inflater, container, false)

        updateTextView_MYDATA()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    private fun updateTextView_MYDATA() {
        class myData : Thread() {
            @SuppressLint("SetTextI18n")
            override fun run() {
                while (true) {
                    sleep(updateRate) //sleep 200 milliseconds
                    activity?.runOnUiThread {
                        binding.textviewSecond.text =
                            "${myData[0]} ${myData[1]} ${myData[2]}"
                    }
                }
            }
        }
        val thread = Thread(myData())
        thread.start()
    }
}