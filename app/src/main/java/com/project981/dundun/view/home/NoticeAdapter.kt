package com.project981.dundun.view.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project981.dundun.R
import com.project981.dundun.databinding.NoticeItemBinding
import com.project981.dundun.model.dto.NoticeDisplayDTO
import java.text.SimpleDateFormat
import java.util.Locale

class NoticeAdapter(val noticeList: ArrayList<NoticeDisplayDTO>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // Adapter 안에 사용할 뷰홀더를 넣어야하는거 아닌지?

    inner class NoticeViewHolder(val binding: NoticeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeDisplayDTO) {
            val dateFormat = "yyyy년 MM월 dd일 E요일 a h:m"
            if (item.date != null) {
                val date = item.date
                val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendarDate: String = simpleDateFormat.format(date)
                binding.txtNoticeDate.visibility = View.VISIBLE
                binding.txtNoticeDate.text = calendarDate
            } else {
                binding.txtNoticeDate.visibility = View.GONE
            }
            Glide.with(itemView)
                .load(item.profileImageUrl)
                .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
                .error(R.drawable.dundun_logo)
                .into(binding.imgNoticeProfile)
            binding.txtNoticeArtistName.text = item.artistName

            if (item.contentImageURL != null) {
                binding.imgNoticeContent.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(item.profileImageUrl)
                    .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
                    .error(R.drawable.dundun_logo)
                    .into(binding.imgNoticeContent)
            } else {
                binding.imgNoticeContent.visibility = View.GONE
            }
            if (item.locationDescription != null) {
                binding.txtNoticeLocation.visibility = View.VISIBLE
                binding.imgNoticeIcon.visibility = View.VISIBLE
                binding.txtNoticeLocation.text = item.locationDescription
            } else {
                binding.imgNoticeIcon.visibility = View.GONE
                binding.txtNoticeLocation.visibility = View.GONE
            }
            binding.txtNoticeContent.text = item.content
            binding.txtNoticeCreateAgo.text =
                if (System.currentTimeMillis() - item.createDate.time <= 60000) {
                    "${(System.currentTimeMillis() - item.createDate.time)}초 전"
                } else if (System.currentTimeMillis() - item.createDate.time <= 360000) {
                    "${(System.currentTimeMillis() - item.createDate.time) / 60000}분 전"
                } else if (System.currentTimeMillis() - item.createDate.time <= 86400000) {
                    "${(System.currentTimeMillis() - item.createDate.time) / 360000}시간 전"
                } else {
                    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
                    simpleDateFormat.format(item.createDate.time)
                }
            binding.txtNoticeLikeCount.text = item.likeCount.toString()

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder /*NoticeAdapter.NoticeViewHolder*/ {
        return NoticeViewHolder(
            NoticeItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoticeViewHolder).bind(noticeList[position])
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

}