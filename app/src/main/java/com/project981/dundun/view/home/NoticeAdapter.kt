package com.project981.dundun.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project981.dundun.R
import com.project981.dundun.databinding.NoticeItemBinding
import com.project981.dundun.model.dto.NoticeDisplayDTO
import java.text.SimpleDateFormat

class NoticeAdapter(val noticeList: ArrayList<NoticeDisplayDTO>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                                                                    // Adapter 안에 사용할 뷰홀더를 넣어야하는거 아닌지?

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder /*NoticeAdapter.NoticeViewHolder*/ {
        return NoticeViewHolder(NoticeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoticeViewHolder).bind(noticeList[position])
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

}