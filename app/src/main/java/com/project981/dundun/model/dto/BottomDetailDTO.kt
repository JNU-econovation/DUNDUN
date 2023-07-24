package com.project981.dundun.model.dto

import java.util.Date

data class BottomDetailDTO(
    val artistID: String,
    val articleID: String,
    val artistName: String,
    val locationDescription: String?,
    val date: Date,
){
    fun copyObj():BottomDetailDTO{
        return BottomDetailDTO(artistID, articleID, artistName, locationDescription, date)
    }
}
