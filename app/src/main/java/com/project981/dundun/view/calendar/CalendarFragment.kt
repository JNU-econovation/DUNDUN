package com.project981.dundun.view.calendar

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentCalendarBinding
import com.project981.dundun.view.MainViewModel
import com.project981.dundun.view.map.BottomRecyclerAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding
        get() = requireNotNull(_binding)
    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var dialog: Dialog
    private val mainViewModel : MainViewModel by activityViewModels()
    private val monthList = listOf(
        "",
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val date = Date(System.currentTimeMillis())

        dialog = Dialog(requireContext());       // Dialog 초기화
        dialog.setContentView(R.layout.calendar_monthpicker)
        dialog.findViewById<NumberPicker>(R.id.pricker_pickerdial_month).apply {
            maxValue = 12
            minValue = 1
            value = SimpleDateFormat("M", Locale.getDefault()).format(date).toInt()
        }
        dialog.findViewById<NumberPicker>(R.id.pricker_pickerdial_year).apply {
            maxValue = 2099
            minValue = 1900
            value = SimpleDateFormat("yyyy", Locale.getDefault()).format(date).toInt()
        }
        if (viewModel.yearAndMonthLive.value == null) {
            viewModel.setYearAndMonth(
                SimpleDateFormat("M", Locale.getDefault()).format(date).toInt(),
                SimpleDateFormat("yyyy", Locale.getDefault()).format(date).toInt()
            )
        }
        val recyclerAdapter = BottomRecyclerAdapter{ noticeId, artistId ->
            mainViewModel.focusItem = noticeId
            mainViewModel.focusArtist = artistId
            findNavController().navigate(R.id.action_calendarFragment_to_myPageFragment)
        }
        binding.recyclerCalendarList.apply {
            adapter = recyclerAdapter
            val linearLayoutManager = LinearLayoutManager(requireContext())

            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            layoutManager = linearLayoutManager
        }

        binding.btnCalendarMonth.setOnClickListener {
            showDialog()
        }


        binding.calendarCalendarView.addTouchCallbackListener {
            viewModel.setEventList(it)
        }

        viewModel.yearAndMonthLive.observe(viewLifecycleOwner) { monthAndYear ->
            binding.btnCalendarMonth.text =
                "${monthList[monthAndYear.first]} ${monthAndYear.second.toString()}"
            viewModel.getEventList(monthAndYear.first, monthAndYear.second) {
                binding.calendarCalendarView.changedCalendar(
                    it,
                    monthAndYear.first,
                    monthAndYear.second
                )
            }
        }

        viewModel.eventList.observe(viewLifecycleOwner) {
            recyclerAdapter.setDate(it ?: listOf())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog() {

        dialog.findViewById<Button>(R.id.btn_pickerdial_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.btn_pickerdial_confirm).setOnClickListener {
            val month = dialog.findViewById<NumberPicker>(R.id.pricker_pickerdial_month).value
            val year = dialog.findViewById<NumberPicker>(R.id.pricker_pickerdial_year).value
            viewModel.setYearAndMonth(month, year)
            dialog.dismiss()
        }


        dialog.show()
    }

}