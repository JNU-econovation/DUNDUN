package com.project981.dundun.model.dto

import java.util.Date

data class NoticeDisplayDTO(
    val artistId: String,
    val articleId: String,
    val artistName: String,
    val profileImageUrl: String,
    val createDate: Date,
    val contentImageURL: String?,
    val content: String,
    val locationDescription: String?,
    val date: Date?,
    var isLike: Boolean
    ) {
}
