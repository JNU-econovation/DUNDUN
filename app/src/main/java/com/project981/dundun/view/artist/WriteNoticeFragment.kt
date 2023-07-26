package com.project981.dundun.view.artist

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.project981.dundun.databinding.FragmentMypageBinding
import com.project981.dundun.databinding.FragmentNoticewriteBinding
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask

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

        val calendar = Calendar.getInstance()
        val datePick = binding.datePickerWrite.setOnDateChangedListener { datePicker, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }
        val timePick = binding.timePickerWrite.setOnTimeChangedListener { timePicker, hour, min ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, min)
        }


        
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }
        val timePicker = TimePickerDialog.OnTimeSetListener { picker, hour, min ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, min)
        }
        binding.btnWriteNoticeSubmit.setOnClickListener {
            DatePickerDialog(requireContext(), datePicker, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

            TimePickerDialog(requireContext(), timePicker, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}