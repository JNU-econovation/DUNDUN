package com.project981.dundun.model.dto.firebase

import com.google.firebase.Timestamp

data class UserDTO(
    val followList: List<String>,
    val likeList: List<String>,
    val name: String,
    val registerTime: Timestamp,
    val updateTime: Timestamp
)