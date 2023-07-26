package com.project981.dundun.model.dto

import android.graphics.Bitmap
import java.util.Date

data class NoticeGetDTO(
    val contentImage: String?,
    val content: String,
    val locationDescription: String?,
    val latitude: Double?,
    val longitude: Double?,
    val date: Date?,
)
