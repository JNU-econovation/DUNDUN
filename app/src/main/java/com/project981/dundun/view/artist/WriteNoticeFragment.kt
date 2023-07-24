package com.project981.dundun.view.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project981.dundun.databinding.FragmentMypageBinding

class WriteNoticeFragment : Fragment() {
    private var _binding : FragmentMypageBinding? = null
    private val binding : FragmentMypageBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //do something
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}