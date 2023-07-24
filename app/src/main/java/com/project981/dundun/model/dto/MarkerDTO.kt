package com.project981.dundun.model.dto

data class MarkerDTO(
    val noticeList: MutableList<BottomDetailDTO>,
    var lng: Double,
    var lat: Double,
    var count: Int,
)
