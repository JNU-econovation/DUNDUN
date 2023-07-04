package com.project981.dundun.model.dto

import java.util.Date

data class NoticeDisplayDTO(
    val articleId: String,
    val artistName: String,
    val profileImageUrl: String,
    val createDate: Date,
    val contentImageURL: String,
    val noticeTitle: String,
    val content: String,
    val locationDescription: String,
    val date: Date,
    val likeCount: Int,
    )
