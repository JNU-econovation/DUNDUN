package com.project981.dundun.view.setting

import android.app.Dialog
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentSettingBinding
import com.project981.dundun.view.MainViewModel
import net.daum.mf.map.api.MapView
import org.w3c.dom.Text

class SettingFragment : Fragment() {
    private var _binding : FragmentSettingBinding? = null
    private val binding : FragmentSettingBinding
        get() = requireNotNull(_binding)

    private val viewModel: SettingViewModel by viewModels()
    private val mainViewModel : MainViewModel by activityViewModels()

    private lateinit var dialog: Dialog

    lateinit var change : (uri : Uri)->(Unit)

    var launcher = registerForActivityResult<String, Uri>(ActivityResultContracts.GetContent()) {
        change(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //do something

        dialog = Dialog(requireContext());       // Dialog 초기화
        dialog.setContentView(R.layout.edit_my_profile)

        binding.txtSettingNameChange.setOnClickListener {
            showDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog() {
        change = {
            Glide.with(this)
                .load(it)
                .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
                .error(R.drawable.dundun_logo)
                .into(
                    dialog.findViewById<ImageView>(R.id.edit_img_profile)
                )
        }


        dialog.findViewById<ImageView>(R.id.edit_img_profile).setOnClickListener {

            launcher.launch("image/*")
        }
        dialog.findViewById<Button>(R.id.btn_edit_submit).setOnClickListener {
            viewModel.changeArtistInfo(
                dialog.findViewById<EditText>(R.id.edit_txt_name).text.toString(),
                (dialog.findViewById<ImageView>(R.id.edit_img_profile).drawable as BitmapDrawable).bitmap,
                dialog.findViewById<EditText>(R.id.edit_img_description).text.toString(),
                mainViewModel._isArtist.value!!
            ) { isSuccess : Boolean ->
                if(isSuccess) {
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }
}