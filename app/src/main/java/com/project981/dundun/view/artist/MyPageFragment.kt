package com.project981.dundun.view.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentMypageBinding
import com.project981.dundun.view.MainViewModel

class MyPageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding: FragmentMypageBinding
        get() = requireNotNull(_binding)

    private val viewModel: ProfileViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //do something

        val listener = { check: CheckBox, noticeID: String ->
            viewModel.changeNoticeLike(noticeID, check.isChecked) {
                check.isChecked = it
            }
        }

        val change = { btn: Button ->
            viewModel.changeArtistFollow(mainViewModel.getFocus().first, btn.text == "Unfollow") {
                if (it.not()) {
                    btn.setText("Follow")
                    btn.setBackgroundColor(resources.getColor(R.color.base_primary_color))
                } else {
                    btn.setText("Unfollow")
                    btn.setBackgroundColor(resources.getColor(R.color.profile_follow_btn_enable))
                }
            }
        }

        val check = { btn: Button ->
            viewModel.getArtistIsFollow(mainViewModel.getFocus().first) {
                if (it.not()) {
                    btn.setText("Follow")
                    btn.setBackgroundColor(resources.getColor(R.color.base_primary_color))
                } else {
                    btn.setText("Unfollow")
                    btn.setBackgroundColor(resources.getColor(R.color.profile_follow_btn_enable))
                }
            }
        }

        val edit = { noticeId: String ->
            mainViewModel.setEditFocus(noticeId)
            findNavController().navigate(R.id.action_myPageFragment_to_writeNoticeFragment)

        }
        val pageAdapter = PageAdapter(
            mainViewModel.getFocus().first == mainViewModel.getIsArtist(),
            listener,
            change,
            check,
            edit
        )

        binding.recyclerProfileList.apply {
            adapter = pageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.getProfileTop(mainViewModel.getFocus().first) {
            viewModel.getArtistNotice(it, mainViewModel.getFocus().first)
        }

        viewModel._list.observe(viewLifecycleOwner) {
            pageAdapter.updateList(it)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}