package com.project981.dundun.view.search

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.project981.dundun.databinding.FragmentSearchBinding
import com.project981.dundun.model.dto.ProfileTopDTO


class SearchFragment : Fragment()  {
    private var _binding : FragmentSearchBinding? = null
    private val binding : FragmentSearchBinding
        get() = requireNotNull(_binding)
    private val searchViewModel : SearchViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //do something
        val userAdapter = UserAdapter()

        searchViewModel.getArtistList( ""){
            userAdapter.setData(it)
        }

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                searchViewModel.getArtistList(newText ?: ""){
                    userAdapter.setData(it)
                }
                return true
            }

        })

        binding.recyclerSearchList.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}