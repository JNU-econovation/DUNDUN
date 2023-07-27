package com.project981.dundun.view.setting

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentSettingBinding
import com.project981.dundun.view.MainViewModel


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
        //do somethin

        dialog = Dialog(requireContext());       // Dialog 초기화
        dialog.setContentView(R.layout.edit_my_profile)

        binding.txtSettingNameChange.setOnClickListener {
            showDialog()
        }
        mainViewModel._isArtist.observe(viewLifecycleOwner) {
            if(it==null) {
                binding.txtSettingNameChange.visibility = View.GONE
                binding.dotSettingNameChange.visibility = View.GONE
            }
        }

        binding.textView.setOnClickListener {
            Firebase.auth.signOut()
            val intent: Intent = requireContext().getPackageManager()
                .getLaunchIntentForPackage(requireContext().getPackageName())!!
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finishAffinity()
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