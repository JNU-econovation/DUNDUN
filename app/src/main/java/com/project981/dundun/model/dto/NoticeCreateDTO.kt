package com.project981.dundun.model.dto

import android.graphics.Bitmap
import com.google.firebase.Timestamp
import java.util.Date

data class NoticeCreateDTO(
    val artistId: String,
    val noticeContent: String,
    val noticeImage: Bitmap?,
    val lat: Double?,
    val lng: Double?,
    val locationDescription: String?,
    val date: Date?
)