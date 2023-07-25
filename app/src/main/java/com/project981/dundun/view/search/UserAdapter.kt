package com.project981.dundun.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project981.dundun.R
import com.project981.dundun.databinding.ItemSearchBinding
import com.project981.dundun.model.dto.ProfileTopDTO

class UserAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemList = listOf<ProfileTopDTO>()

    inner class UserViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : ProfileTopDTO) {
            binding.txtSearchArtistName.text = item.artistName
            Glide.with(itemView)
                .load(item.profileImageUrl)
                .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo).error(R.drawable.dundun_logo)
                .into(binding.imgSearchProfile)
            binding.txtSearchDescription.text = item.artistDescription
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserViewHolder).bind(itemList[position])
    }

    fun setData(list: List<ProfileTopDTO>) {
        itemList = list
        notifyDataSetChanged()
    }
}