package com.project981.dundun.model.dto

import android.graphics.Bitmap
import java.util.Date

data class NoticeChangeDTO (
    val contentImage: Bitmap?,
    val content: String,
    val locationDescription: String?,
    val latitude: Double?,
    val longitude: Double?,
    val date: Date?,
    )