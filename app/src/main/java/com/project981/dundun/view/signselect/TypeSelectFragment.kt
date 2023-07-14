package com.project981.dundun.view.signselect

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.project981.dundun.R

class TypeSelectFragment : Fragment() {

    val viewModel: TypeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_type_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<AppCompatButton>(R.id.btn_sign_select_artist).setOnClickListener {
            viewModel.setArtist(true)
            findNavController().navigate(R.id.action_typeSelectFragment_to_signUpFragment)
        }

        view.findViewById<AppCompatButton>(R.id.btn_sign_select_user).setOnClickListener {

            viewModel.setArtist(false)
            findNavController().navigate(R.id.action_typeSelectFragment_to_signUpFragment)
        }
    }
}