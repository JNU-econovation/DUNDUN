package com.project981.dundun.model.dto

import java.util.Date

data class NoticeChangeDTO (
    val changeDate: Date,
    val contentImageURL: String,
    val noticeTitle: String,
    val content: String,
    val locationDescription: String,
    val latitude: Double,
    val longitude: Double,
    val date: Date,
    )