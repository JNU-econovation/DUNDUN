package com.project981.dundun.model.dto.firebase

import com.google.firebase.Timestamp

data class ArtistDTO(
    val uid: String,
    val articleList: List<String>,
    val artistName: String,
    val description: String,
    val profileImageUrl: String,
    val registerTime: Timestamp
)
