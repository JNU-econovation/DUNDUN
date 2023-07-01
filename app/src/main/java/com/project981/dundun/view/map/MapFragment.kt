package com.project981.dundun.view.map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentHomeBinding
import com.project981.dundun.databinding.FragmentMapBinding

class MapFragment : Fragment() {
    private var _binding : FragmentMapBinding? = null
    private val binding : FragmentMapBinding
        get() = requireNotNull(_binding)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(layoutInflater)
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