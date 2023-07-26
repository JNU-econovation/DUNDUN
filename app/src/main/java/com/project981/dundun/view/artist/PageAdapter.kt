package com.project981.dundun.view.artist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project981.dundun.R
import com.project981.dundun.databinding.AddMyProfileBinding
import com.project981.dundun.databinding.NoticeItemBinding
import com.project981.dundun.model.dto.NoticeDisplayDTO
import com.project981.dundun.model.dto.ProfileTopDTO
import java.text.SimpleDateFormat
import java.util.Locale

class PageAdapter(
    val isEnable: Boolean,
    val clickListener: (CheckBox, String) -> Unit,
    val change: (Button) -> Unit,
    val check: (Button) -> Unit,
    val edit: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val MULTI_PROFILE = 0
    val MULTI_NOTICE = 1

    inner class InfoViewHolder(val binding: AddMyProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfileTopDTO) {
            check(binding.btnWriteNoticeDelete)
            binding.btnWriteNoticeDelete.isEnabled = !isEnable
            if(isEnable){
                binding.btnWriteNoticeDelete.text = "Yours"
            }
            binding.btnWriteNoticeDelete.setOnClickListener {
                change(binding.btnWriteNoticeDelete)
            }

            binding.txtMypageName.text = item.artistName
            Glide.with(itemView)
                .load(item.profileImageUrl)
                .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
                .error(R.drawable.dundun_logo)
                .into(binding.imgMypageProfile)
            binding.txtMypageDescription.text = item.artistDescription

        }
    }

    inner class NoticeViewHolder(val binding: NoticeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NoticeDisplayDTO) {
            binding.iconNoticeSetting.visibility = if(isEnable) View.VISIBLE else View.GONE
            binding.checkboxNoticeLike.isChecked = item.isLike
            binding.checkboxNoticeLike.setOnCheckedChangeListener { _, b ->
                item.isLike = b
                clickListener(
                    binding.checkboxNoticeLike,
                    item.articleId
                )
            }
            binding.iconNoticeSetting.setOnClickListener {
                edit(item.articleId)
            }
            binding.txtNoticeContent.text = item.content
            itemView.post {
                if (binding.txtNoticeContent.lineCount >= 5) {
                    binding.btnNoticeMore.visibility = View.VISIBLE
                } else {
                    binding.btnNoticeMore.visibility = View.GONE
                }

                binding.btnNoticeMore.setOnClickListener {
                    if (binding.txtNoticeContent.maxLines == 5) {
                        binding.btnNoticeMore.text = "close"
                        binding.txtNoticeContent.maxLines = 100
                    } else {

                        binding.btnNoticeMore.text = "more"
                        binding.txtNoticeContent.maxLines = 5
                    }
                }
            }
            val dateFormat = "yyyy년 MM월 dd일 E요일 a h:m"
            if (item.date != null) {
                val date = item.date
                val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
                val calendarDate: String = simpleDateFormat.format(date)
                binding.txtNoticeDate.visibility = View.VISIBLE
                binding.imgNoticeIcon.visibility = View.VISIBLE
                binding.txtNoticeDate.text = calendarDate
            } else {
                binding.txtNoticeDate.visibility = View.GONE
                binding.imgNoticeIcon.visibility = View.GONE
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
                    .load(item.contentImageURL)
                    .fallback(R.drawable.dundun_logo).placeholder(R.drawable.dundun_logo)
                    .error(R.drawable.dundun_logo)
                    .into(binding.imgNoticeContent)
            } else {
                binding.imgNoticeContent.visibility = View.GONE
            }
            if (item.locationDescription != null) {
                binding.txtNoticeLocation.visibility = View.VISIBLE
                binding.txtNoticeLocation.text = item.locationDescription
            } else {
                binding.txtNoticeLocation.visibility = View.GONE
            }

            binding.txtNoticeCreateAgo.text =
                if (System.currentTimeMillis() - item.createDate.time <= 60000) {
                    "${(System.currentTimeMillis() - item.createDate.time)/1000}초 전"
                } else if (System.currentTimeMillis() - item.createDate.time <= 3600000) {
                    "${(System.currentTimeMillis() - item.createDate.time) / 60000}분 전"
                } else if (System.currentTimeMillis() - item.createDate.time <= 86400000) {
                    "${(System.currentTimeMillis() - item.createDate.time) / 3600000}시간 전"
                } else {
                    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.getDefault())
                    simpleDateFormat.format(item.createDate.time)
                }
        }
    }

    private val itemList = arrayListOf<Any>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MULTI_PROFILE -> InfoViewHolder(
                AddMyProfileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            MULTI_NOTICE -> NoticeViewHolder(
                NoticeItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw RuntimeException("Invalid ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is InfoViewHolder -> holder.bind(itemList[position] as ProfileTopDTO)
            is NoticeViewHolder -> holder.bind(itemList[position] as NoticeDisplayDTO)
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position]) {
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