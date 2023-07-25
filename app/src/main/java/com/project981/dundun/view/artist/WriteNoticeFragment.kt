package com.project981.dundun.view.artist

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.project981.dundun.databinding.FragmentMypageBinding
import com.project981.dundun.databinding.FragmentNoticewriteBinding
import java.util.Calendar

class WriteNoticeFragment : Fragment() {
    private var _binding : FragmentNoticewriteBinding? = null
    private val binding : FragmentNoticewriteBinding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoticewriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //do something
        binding.switchCalender.setOnCheckedChangeListener{ _, onSwitch ->
            if (!onSwitch) {
                binding.layoutDetailCalender.visibility = View.GONE

            } else {
                binding.layoutDetailCalender.visibility = View.VISIBLE
            }
        }

        binding.switchMap.setOnCheckedChangeListener{ _, onSwitch ->
            if (!onSwitch) {
                binding.layoutDetailMap.visibility = View.GONE

            } else {
                binding.layoutDetailMap.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}