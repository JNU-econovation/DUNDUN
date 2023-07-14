package com.project981.dundun.view.signselect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import com.project981.dundun.R

class SignSelectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<AppCompatButton>(R.id.btn_sign_select_signup).setOnClickListener {
            findNavController().navigate(R.id.action_signSelectFragment_to_typeSelectFragment)
        }

        view.findViewById<AppCompatButton>(R.id.btn_sign_select_signin).setOnClickListener {
            findNavController().navigate(R.id.action_signSelectFragment_to_signInFragment)
        }
    }

}