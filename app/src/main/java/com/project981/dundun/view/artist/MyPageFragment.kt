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
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentMypageBinding
import com.project981.dundun.model.dto.NoticeDisplayDTO
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
            mainViewModel.setProgress(true)
            viewModel.changeArtistFollow(mainViewModel.focusArtist!!, btn.text == "Following") {
                mainViewModel.setProgress(false)
                if (it.not()) {
                    btn.text = "Follow"
                    btn.background = resources.getDrawable(R.drawable.my_follow_shape_f)
                } else {
                    btn.text = "Following"
                    btn.background = resources.getDrawable(R.drawable.my_follow_shape_t)
                }
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            (binding.recyclerProfileList.adapter as PageAdapter).updateList(listOf())
            viewModel.getProfileTop(mainViewModel.focusArtist!!) {
                viewModel.getArtistNotice(it, mainViewModel.focusArtist!!)
            }
        }
        val check = { btn: Button ->
            viewModel.getArtistIsFollow(mainViewModel.focusArtist!!) {
                if (it.not()) {
                    btn.text = "Follow"
                    btn.background = resources.getDrawable(R.drawable.my_follow_shape_f)
                } else {
                    btn.text = "Following"
                    btn.background = resources.getDrawable(R.drawable.my_follow_shape_t)
                }
            }
        }

        val edit = { noticeId: String ->
            mainViewModel.editFocus = noticeId
            findNavController().navigate(R.id.action_myPageFragment_to_writeNoticeFragment)

        }
        val pageAdapter = PageAdapter(
            mainViewModel.focusArtist!! == mainViewModel._isArtist.value,
            listener,
            change,
            check,
            edit
        )

        binding.recyclerProfileList.apply {
            adapter = pageAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        mainViewModel.setProgress(true)
        viewModel.getProfileTop(mainViewModel.focusArtist!!) {

            viewModel.getArtistNotice(it, mainViewModel.focusArtist!!)
        }

        viewModel._list.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = false
            mainViewModel.setProgress(false)
            pageAdapter.updateList(it)
            binding.recyclerProfileList.post {
                if(mainViewModel.focusItem != null){
                    for(i in it.indices){
                        if(it[i] is NoticeDisplayDTO && (it[i] as NoticeDisplayDTO).articleId == mainViewModel.focusItem){
                            val smoothScroller: SmoothScroller by lazy {
                                object : LinearSmoothScroller(requireContext()){
                                    override fun getVerticalSnapPreference() = SNAP_TO_START
                                }
                            }
                            smoothScroller.targetPosition = i
                            binding.recyclerProfileList.layoutManager?.startSmoothScroll(smoothScroller)
                            break
                        }
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}