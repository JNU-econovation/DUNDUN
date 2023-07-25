package com.project981.dundun.model.dto.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class NoticeDTO(
    val artistId: String,
    val noticeContent: String,
    val noticeImage: String?,
    val date: Timestamp?,
    val geo: GeoPoint?,
    val geoHash: String?,
    val locationDescription: String?,
    val createTime: Timestamp,
    val updateTime: Timestamp,
)
