package com.project981.dundun.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.animation.AnimationUtils
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentHomeBinding
import com.project981.dundun.view.artist.MyPageFragment
import com.project981.dundun.view.artist.WriteNoticeFragment

class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null

    private val rotateOpen: Animation by lazy { android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }
    private var clicked = false

    private val binding : FragmentHomeBinding
        get() = requireNotNull(_binding)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //do something

        binding.iconNoticeAdd.setOnClickListener {
            onAddButtonClicked()
        }

        binding.iconNoticeProfile.setOnClickListener {
            onAddButtonClicked()
            Toast.makeText(requireContext(), "My Profile", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }

        binding.iconNoticeWrite.setOnClickListener {
            onAddButtonClicked()
            Toast.makeText(requireContext(), "Write Notice", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_homeFragment_to_writeNoticeFragment)
        }
    }

    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.iconNoticeWrite.visibility = View.VISIBLE
            binding.iconNoticeProfile.visibility = View.VISIBLE
        } else {
            binding.iconNoticeWrite.visibility = View.INVISIBLE
            binding.iconNoticeProfile.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.iconNoticeWrite.startAnimation(fromBottom)
            binding.iconNoticeProfile.startAnimation(fromBottom)
            binding.iconNoticeAdd.startAnimation(rotateOpen)
        } else {
            binding.iconNoticeWrite.startAnimation(toBottom)
            binding.iconNoticeProfile.startAnimation(toBottom)
            binding.iconNoticeAdd.startAnimation(rotateClose)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if (clicked) {
            binding.iconNoticeWrite.isClickable = false
            binding.iconNoticeProfile.isClickable = false
        } else {
            binding.iconNoticeWrite.isClickable = true
            binding.iconNoticeProfile.isClickable = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}