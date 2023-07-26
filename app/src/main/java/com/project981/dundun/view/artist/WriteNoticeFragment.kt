package com.project981.dundun.view.artist

import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentNoticewriteBinding
import com.project981.dundun.model.dto.NoticeChangeDTO
import com.project981.dundun.model.dto.NoticeCreateDTO
import com.project981.dundun.view.MainViewModel
import net.daum.mf.map.api.MapView
import java.util.Calendar

class WriteNoticeFragment : Fragment() {
    private var _binding: FragmentNoticewriteBinding? = null
    private val binding: FragmentNoticewriteBinding
        get() = requireNotNull(_binding)

    private val viewModel: WriteNoticeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var dialog: Dialog

    var launcher = registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()) {
        Glide.with(this)
            .load(it)
            .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
            .error(R.drawable.dundun_logo)
            .into(binding.imgWriteNoticeContent)
    }

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

        binding.edittxtWriteMapDescription.isEnabled = false
        dialog = Dialog(requireContext());       // Dialog 초기화
        dialog.setContentView(R.layout.dialog_map)

        viewModel.getProfileTop(mainViewModel._isArtist.value!!) {
            Glide.with(this)
                .load(it.profileImageUrl)
                .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
                .error(R.drawable.dundun_logo)
                .into(binding.imgWriteNoticeProfile)
            binding.txtWriteNoticeName.text = it.artistName

        }

        if (mainViewModel.editFocus != null) {
            viewModel.getNotice(mainViewModel.editFocus!!) {
                binding.txtLat.text = it.latitude?.toString() ?: ""
                binding.txtLng.text = it.longitude?.toString() ?: ""
                binding.edittxtWriteMapDescription.setText(it.locationDescription ?: "")
                binding.edittxtWirteNotice.setText(it.content)
                if (it.date != null) {
                    binding.switchCalender.isChecked = true
                    binding.datePickerWrite.updateDate(
                        DateFormat.format("yyyy", it.date).toString().toInt(),
                        DateFormat.format("MM", it.date).toString().toInt(),
                        DateFormat.format("dd", it.date).toString().toInt()
                    )
                    binding.timePickerWrite.hour = (
                            DateFormat.format("hh", it.date).toString().toInt()
                            )
                    binding.timePickerWrite.minute = (
                            DateFormat.format("mm", it.date).toString().toInt()
                            )

                } else {
                    binding.switchCalender.isChecked = false
                }
                Glide.with(this)
                    .load(it.contentImage)
                    .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
                    .error(R.drawable.dundun_logo)
                    .into(binding.imgWriteNoticeContent)
                binding.switchMap.isChecked = it.latitude != null && it.longitude != null
                binding.edittxtWriteMapDescription.isEnabled =
                    it.latitude != null && it.longitude != null
            }
        }

        binding.iconRemove.visibility =
            if (mainViewModel.editFocus == null) View.GONE else View.VISIBLE

        binding.switchCalender.setOnCheckedChangeListener { _, onSwitch ->
            if (!onSwitch) {
                binding.layoutDetailCalender.visibility = View.GONE

            } else {
                binding.layoutDetailCalender.visibility = View.VISIBLE
            }
        }

        binding.switchMap.setOnCheckedChangeListener { _, onSwitch ->
            if (!onSwitch) {
                binding.layoutDetailMap.visibility = View.GONE

            } else {
                binding.layoutDetailMap.visibility = View.VISIBLE
            }
        }

        val calendar = Calendar.getInstance()

        binding.datePickerWrite.setOnDateChangedListener { datePicker, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }

        binding.timePickerWrite.setOnTimeChangedListener { timePicker, hour, min ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, min)
        }

        binding.btnSelectLocation.setOnClickListener {
            showDialog()
        }
        binding.btnWriteNoticeSubmit.setOnClickListener {

            if (mainViewModel.editFocus == null) {

                viewModel.createNotice(
                    NoticeCreateDTO(
                        mainViewModel._isArtist.value!!,
                        binding.edittxtWirteNotice.text.toString(),
                        if (binding.imgWriteNoticeContent.drawable is VectorDrawable) null else (binding.imgWriteNoticeContent.drawable as BitmapDrawable).bitmap,
                        if (binding.switchMap.isChecked && binding.txtLat.text.isNotEmpty()) (binding.txtLat.text.toString()).toDouble() else null,
                        if (binding.switchMap.isChecked && binding.txtLng.text.isNotEmpty()) (binding.txtLng.text.toString()).toDouble() else null,
                        if (binding.switchMap.isChecked && binding.edittxtWriteMapDescription.text.isNotEmpty()) binding.edittxtWriteMapDescription.text.toString() else null,
                        if (binding.switchCalender.isChecked) calendar.time else null,
                    )
                ) {
                    if (it) {
                        findNavController().popBackStack()
                    }
                }
            } else {
                viewModel.editNotice(
                    NoticeChangeDTO(
                        if (binding.imgWriteNoticeContent.drawable is VectorDrawable) null else (binding.imgWriteNoticeContent.drawable as BitmapDrawable).bitmap,
                        binding.edittxtWirteNotice.text.toString(),
                        if (binding.switchMap.isChecked && binding.edittxtWriteMapDescription.text.isNotEmpty()) binding.edittxtWriteMapDescription.text.toString() else null,
                        if (binding.switchMap.isChecked && binding.txtLat.text.isNotEmpty()) (binding.txtLat.text.toString()).toDouble() else null,
                        if (binding.switchMap.isChecked && binding.txtLng.text.isNotEmpty()) (binding.txtLng.text.toString()).toDouble() else null,
                        if (binding.switchCalender.isChecked) calendar.time else null,

                        ),
                    mainViewModel.editFocus!!
                ) {
                    if (it) {
                        findNavController().popBackStack()
                    }
                }
            }
        }

        binding.btnAddNoticeImg.setOnClickListener {
            launcher.launch("image/*")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog() {
        val mapView = MapView(activity)
        dialog.findViewById<RelativeLayout>(R.id.map_map_dialog).addView(mapView)

        dialog.findViewById<Button>(R.id.btn_map_dialog).setOnClickListener {
            binding.edittxtWriteMapDescription.isEnabled = true
            binding.txtLat.text = mapView.mapCenterPoint.mapPointGeoCoord.latitude.toString()
            binding.txtLng.text = mapView.mapCenterPoint.mapPointGeoCoord.longitude.toString()
            dialog.dismiss()
        }

        dialog.show()
    }
}