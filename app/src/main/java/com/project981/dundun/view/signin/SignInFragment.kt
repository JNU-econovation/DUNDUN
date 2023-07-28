package com.project981.dundun.view.signin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.project981.dundun.R
import com.project981.dundun.SignActivity
import com.project981.dundun.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null;
    private val binding: FragmentSignInBinding
        get() = requireNotNull(_binding)

    private val viewModel: SigninViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSigninSubmit.setOnClickListener {
            if (binding.edittxtSigninId.text.toString().isNotBlank() &&
                binding.edittxtSigninPw.text.toString().isNotBlank()
            ) {
                (activity as SignActivity).findViewById<LinearLayout>(R.id.progress).visibility = View.VISIBLE
                viewModel.submitSignIn(
                    binding.edittxtSigninId.text.toString(),
                    binding.edittxtSigninPw.text.toString()
                ){result ->

                    (activity as SignActivity).findViewById<LinearLayout>(R.id.progress).visibility = View.GONE
                    result.onSuccess {
                        Toast.makeText(requireContext(),"로그인에 성공 했습니다",Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(requireContext(),"로그인에 실패 했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(),"모든 칸을 채워주세요.",Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}