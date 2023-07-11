package com.project981.dundun.view.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private var _binding : FragmentSignInBinding? = null;
    private val binding : FragmentSignInBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}