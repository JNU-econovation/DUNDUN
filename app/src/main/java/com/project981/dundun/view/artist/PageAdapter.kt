package com.project981.dundun.view.artist

import android.provider.ContactsContract.Profile
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project981.dundun.R
import com.project981.dundun.databinding.AddMyProfileBinding
import com.project981.dundun.databinding.NoticeItemBinding
import com.project981.dundun.model.dto.NoticeDisplayDTO
import com.project981.dundun.model.dto.ProfileTopDTO
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.text.SimpleDateFormat

class PageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val MULTI_PROFILE = 0
    val MULTI_NOTICE = 1

    inner class InfoViewHolder(val binding: AddMyProfileBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfileTopDTO) {
            binding.txtMypageName.text = item.artistName
            Glide.with(itemView)
                .load(item.profileImageUrl)
                .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo).error(R.drawable.dundun_logo)
                .into(binding.imgMypageProfile)
            binding.txtMypageDescription.text = item.artistDescription

        }
    }

    inner class NoticeViewHolder(val binding: NoticeItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeDisplayDTO) {
            val dateFormat = "yyyy년 MM월 dd일 E요일 a h:m"
            val date = item.date
            val simpleDateFormat = SimpleDateFormat(dateFormat)
            val calendarDate: String = simpleDateFormat.format(date)

            Glide.with(itemView)
                .load(item.profileImageUrl)
                .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo).error(R.drawable.dundun_logo)
                .into(binding.imgNoticeProfile)
            binding.txtNoticeArtistName.text = item.artistName
            binding.txtNoticeLocation.text = item.locationDescription
            binding.txtNoticeDate.text = calendarDate
            binding.txtNoticeContent.text = item.content
            // createDate는 어떻게?
            // binding.txtNoticeCreateAgo.text = item.createDate
            binding.txtNoticeLikeCount.text = item.likeCount.toString()

        }
    }

    private val itemList = arrayListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MULTI_PROFILE -> InfoViewHolder(AddMyProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            MULTI_NOTICE -> NoticeViewHolder(NoticeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw RuntimeException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is InfoViewHolder -> holder.bind(itemList[position] as ProfileTopDTO)
            is NoticeViewHolder -> holder.bind(itemList[position] as NoticeDisplayDTO)
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when(itemList[position]) {
            is ProfileTopDTO -> MULTI_PROFILE
            is NoticeDisplayDTO -> MULTI_NOTICE
            else -> throw RuntimeException("Invalid item")
        }
    }

    fun updateList(updatedList: List<Any>) {
        itemList.clear()
        itemList.addAll(updatedList)
        notifyDataSetChanged()
    }
}