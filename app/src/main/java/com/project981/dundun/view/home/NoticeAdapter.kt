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

class NoticeAdapter(val noticeList: ArrayList<NoticeDisplayDTO>) : RecyclerView.Adapter<NoticeAdapter.CustomViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeAdapter.CustomViewHolder {
        return CustomViewHolder(NoticeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }

    override fun onBindViewHolder(holder: NoticeAdapter.CustomViewHolder, position: Int) {
//        holder.profileImageUrl.setImageResource(noticeList.get(position).profileImageUrl.toInt())
//        holder.profileName.text = noticeList.get(position).artistName
//        holder.poster.setImageResource(noticeList.get(position).contentImageURL.toInt())
//        holder.notice_text.text = noticeList.get(position).content
//        holder.likeCount.text = noticeList.get(position).likeCount.toString()
        holder.bind(noticeList[position])
    }

    class CustomViewHolder(val binding: NoticeItemBinding) : RecyclerView.ViewHolder(binding.root){
//        val profileImageUrl = itemView.findViewById<ImageView>(R.id.profile_imgage)    // 프로필 사진
//        val profileName = itemView.findViewById<TextView>(R.id.profile_name)        // 프로필 이름
//        val poster = itemView.findViewById<ImageView>(R.id.notice_poster)           // 포스터
//        val notice_text = itemView.findViewById<TextView>(R.id.notice_text)         // 게시글 작성
//        val likeImage = itemView.findViewById<ImageView>(R.id.notice_like)          // 좋아요 이미지
//        val likeCount = itemView.findViewById<TextView>(R.id.notice_like_num)      // 좋아요 수

        fun bind (item: NoticeDisplayDTO) {
            //Glide.with(binding.profileImgage.context).load(R.drawable.profile).into()
        }
    }
}