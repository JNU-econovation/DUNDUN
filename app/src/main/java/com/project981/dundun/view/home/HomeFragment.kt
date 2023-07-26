package com.project981.dundun.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentHomeBinding
import com.project981.dundun.view.MainViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val rotateOpen: Animation by lazy {
        android.view.animation.AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        android.view.animation.AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        android.view.animation.AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        android.view.animation.AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }
    private var clicked = false

    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding)
    private val viewModel: HomeViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
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

        if (viewModel.list.value == null) {
            viewModel.getFollowNoticeList()
        }

        val listener = { artistId : String ->
            mainViewModel.focusItem = null
            mainViewModel.focusArtist = artistId
            findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }
        val recyclerAdapter = NoticeAdapter(listener) { check, NoticeID ->
            viewModel.changeNoticeLike(NoticeID, check.isChecked) {
                check.isChecked = it
            }

        }

        mainViewModel._isArtist.observe(viewLifecycleOwner) {

            if(it!=null) {
                binding.iconNoticeWrite.visibility = View.INVISIBLE
                binding.iconNoticeAdd.visibility = View.VISIBLE
                binding.iconNoticeProfile.visibility = View.INVISIBLE
            }
        }
        binding.recyclerView.apply {
            adapter = recyclerAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.iconNoticeAdd.setOnClickListener {
            onAddButtonClicked()
        }

        binding.iconNoticeProfile.setOnClickListener {
            onAddButtonClicked()
            mainViewModel.focusArtist = mainViewModel._isArtist.value
            mainViewModel.focusItem = null
            findNavController().navigate(R.id.action_homeFragment_to_myPageFragment)
        }

        binding.iconNoticeWrite.setOnClickListener {
            onAddButtonClicked()
            mainViewModel.editFocus = null
            findNavController().navigate(R.id.action_homeFragment_to_writeNoticeFragment)
        }


        viewModel.list.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = false
            recyclerAdapter.setDate(it)
        }

        binding.refreshLayout.setOnRefreshListener {
            recyclerAdapter.setDate(listOf())
            viewModel.getFollowNoticeList()
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