package com.project981.dundun.view.signup

import android.animation.ValueAnimator
import android.content.res.Resources.Theme
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentSignUpBinding
import com.project981.dundun.model.dto.firebase.ArtistDTO
import com.project981.dundun.model.dto.firebase.NoticeDTO
import com.project981.dundun.model.dto.firebase.UserDTO
import com.project981.dundun.model.repository.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding: FragmentSignUpBinding
        get() = requireNotNull(_binding)

    private val viewModel: SignupViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignupSubmit.addTextChangedListener {
            if (viewModel.idState.value == IdStateEnum.CORRECT &&
                viewModel.pwState.value == PwStateEnum.CORRECT &&
                viewModel.nameState.value == NameStateEnum.CORRECT &&
                viewModel.pwRepeatState.value == PwRepeatStateEnum.CORRECT
            ) {

                viewModel.submitSignup(
                    binding.edittxtSignupId.text.toString(),
                    binding.edittxtSignupPw.text.toString(),
                    binding.edittxtSignupName.text.toString()
                ) { isSuccess ->
                    if(isSuccess){
                        Toast.makeText(context,"회원가입에 성공했습니다.",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"회원가입에 성공했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context,"모든 칸을 채워주세요.",Toast.LENGTH_SHORT).show()
            }
        }
        addListener()
        addObserver()
    }


    private fun addListener() {
        binding.edittxtSignupId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.changeId(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                //do nothing
            }

        })

        binding.edittxtSignupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.changeName(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                //do nothing
            }

        })

        binding.edittxtSignupPw.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.changePw(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                //do nothing
            }

        })

        binding.edittxtSignupPwrepeat.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.changePwRepeat(p0.toString()) {
                    binding.edittxtSignupPw.text.toString() == binding.edittxtSignupPwrepeat.text.toString()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //do nothing
            }

        })
    }

    private fun addObserver() {
        viewModel.idState.observe(viewLifecycleOwner) {
            when (it) {
                IdStateEnum.NONE -> {
                    binding.checkboxSignupId.apply {
                        height = 0
                        width = 0
                        isChecked = false
                        visibility = View.GONE
                    }
                    binding.txtSignupIdstate.apply {
                        height = 0
                        setTextColor(requireActivity().getColor(R.color.signup_textfield_error_color))
                        visibility = View.INVISIBLE
                    }
                }

                else -> {
                    binding.txtSignupIdstate.text = when (it) {
                        IdStateEnum.CORRECT -> {
                            binding.checkboxSignupId.isChecked = true
                            binding.txtSignupIdstate.setTextColor(requireActivity().getColor(R.color.signup_textfield_color))
                            getString(R.string.signup_id_correct)
                        }

                        IdStateEnum.ERROR -> {
                            getString(R.string.signup_id_error_total)
                        }

                        IdStateEnum.DUPLICATE -> {
                            getString(R.string.signup_id_error_duplicate)
                        }

                        else -> {
                            getString(R.string.signup_id_error_type)
                        }
                    }
                    binding.checkboxSignupId.visibility = View.VISIBLE
                    binding.txtSignupIdstate.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        startAnimate(binding.checkboxSignupId, binding.txtSignupIdstate)
                    }
                }
            }
        }

        viewModel.nameState.observe(viewLifecycleOwner) {
            when (it) {
                NameStateEnum.NONE -> {
                    binding.checkboxSignupName.apply {
                        height = 0
                        width = 0
                        isChecked = false
                        visibility = View.GONE
                    }
                    binding.txtSignupNamestate.apply {
                        height = 0
                        setTextColor(requireActivity().getColor(R.color.signup_textfield_error_color))
                        visibility = View.INVISIBLE
                    }
                }

                else -> {
                    binding.txtSignupNamestate.text = when (it) {
                        NameStateEnum.CORRECT -> {
                            binding.checkboxSignupName.isChecked = true
                            binding.txtSignupNamestate.setTextColor(requireActivity().getColor(R.color.signup_textfield_color))
                            getString(R.string.signup_name_correct)
                        }

                        NameStateEnum.LENGTH -> {
                            getString(R.string.signup_name_error_length)
                        }

                        else -> {
                            getString(R.string.signup_name_error_type)
                        }
                    }
                    binding.checkboxSignupName.visibility = View.VISIBLE
                    binding.txtSignupNamestate.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        startAnimate(binding.checkboxSignupName, binding.txtSignupNamestate)
                    }
                }
            }
        }

        viewModel.pwState.observe(viewLifecycleOwner) {
            when (it) {
                PwStateEnum.NONE -> {
                    binding.checkboxSignupPw.apply {
                        height = 0
                        width = 0
                        isChecked = false
                        visibility = View.GONE
                    }
                    binding.txtSignupPwstate.apply {
                        height = 0
                        setTextColor(requireActivity().getColor(R.color.signup_textfield_error_color))
                        visibility = View.INVISIBLE
                    }
                }

                else -> {
                    binding.txtSignupPwstate.text = when (it) {
                        PwStateEnum.CORRECT -> {
                            binding.checkboxSignupPw.isChecked = true
                            binding.txtSignupPwstate.setTextColor(requireActivity().getColor(R.color.signup_textfield_color))
                            getString(R.string.signup_pw_correct)
                        }

                        PwStateEnum.LENGTH -> {
                            getString(R.string.signup_pw_error_length)
                        }

                        else -> {
                            getString(R.string.signup_pw_error_type)
                        }
                    }
                    binding.checkboxSignupPw.visibility = View.VISIBLE
                    binding.txtSignupPwstate.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        startAnimate(binding.checkboxSignupPw, binding.txtSignupPwstate)
                    }
                }
            }
        }

        viewModel.pwRepeatState.observe(viewLifecycleOwner) {
            when (it) {
                PwRepeatStateEnum.NONE -> {
                    binding.checkboxSignupPwrepeat.apply {
                        height = 0
                        width = 0
                        isChecked = false
                        visibility = View.GONE
                    }
                    binding.txtSignupPwrepeatstate.apply {
                        height = 0
                        setTextColor(requireActivity().getColor(R.color.signup_textfield_error_color))
                        visibility = View.INVISIBLE
                    }
                }

                else -> {
                    binding.txtSignupPwrepeatstate.text = when (it) {
                        PwRepeatStateEnum.CORRECT -> {
                            binding.checkboxSignupPwrepeat.isChecked = true
                            binding.txtSignupPwrepeatstate.setTextColor(requireActivity().getColor(R.color.signup_textfield_color))
                            getString(R.string.signup_pwrepeat_correct)
                        }

                        else -> {
                            getString(R.string.signup_pwrepeat_error_type)
                        }
                    }
                    binding.checkboxSignupPwrepeat.visibility = View.VISIBLE
                    binding.txtSignupPwrepeatstate.visibility = View.VISIBLE
                    CoroutineScope(Dispatchers.Main).launch {
                        startAnimate(binding.checkboxSignupPwrepeat, binding.txtSignupPwrepeatstate)
                    }
                }
            }
        }
    }

    private fun startAnimate(checkBox: CheckBox, text: TextView) {
        ValueAnimator.ofInt(
            0,
            (requireContext().resources.displayMetrics.density * STATE_VIEW_SIZE).toInt()
        ).apply {
            addUpdateListener {
                checkBox.layoutParams =
                    LinearLayout.LayoutParams(
                        it.animatedValue as Int / 3 * 2,
                        it.animatedValue as Int / 3 * 2
                    )
                text.layoutParams = LinearLayout.LayoutParams(text.width, it.animatedValue as Int)

            }
            duration = 300L

        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val STATE_VIEW_SIZE = 32
    }
}