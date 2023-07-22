package com.project981.dundun.model.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project981.dundun.model.dto.BottomDetailDTO
import com.project981.dundun.model.dto.firebase.ArtistDTO
import com.project981.dundun.model.dto.firebase.UserDTO
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

object MainRepository {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val montList = listOf(0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    fun checkIdDuplicate(email: String, callback: (Result<Boolean>) -> (Unit)) {
        Firebase.firestore
            .collection("Email")
            .document(email)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result.exists()) {
                        callback(Result.success(true))
                    } else {
                        callback(Result.success(false))
                    }
                } else {
                    callback(Result.failure(Exception("서버와 연결에 실패했습니다.")))
                }
            }
    }

    fun createUser(
        email: String,
        pw: String,
        name: String,
        isArtist: Boolean,
        callback: (Result<Boolean>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                Firebase.firestore.runBatch {
                    it.set(
                        Firebase.firestore.collection("Email").document(email),
                        mapOf("1" to "1")
                    )

                    it.set(
                        Firebase.firestore.collection("User")
                            .document(auth.currentUser?.uid.toString()),
                        UserDTO(
                            listOf(),
                            listOf(),
                            name,
                            Timestamp.now(),
                            Timestamp.now()
                        )
                    )
                    if (isArtist) {
                        it.set(
                            Firebase.firestore
                                .collection("Artist")
                                .document(),
                            ArtistDTO(
                                auth.currentUser?.uid.toString(),
                                listOf(),
                                name,
                                "",
                                "https://firebasestorage.googleapis.com/v0/b/dundun-625f9.appspot.com/o/base_profile.png?alt=media&token=79cc1280-ed0b-4d4f-9168-8a6919a5667e",
                                Timestamp.now()
                            )
                        )
                    }
                }.addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(Result.success(true))
                    } else {
                        callback(Result.failure(Exception("데이터 생성 실패")))
                        auth.currentUser?.delete()
                    }
                }
            } else {
                callback(Result.failure(Exception("서버와 연결 실페")))
            }
        }
    }

    fun checkSignIn(email: String, pw: String, callback: (Result<Boolean>) -> Unit) {
        auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(Result.success(true))
            } else {
                callback(Result.failure(Exception("로그인 실패")))
            }
        }
    }


    fun getFollowerNotice() {

    }

    fun getFollowerNoticeIdListWithMonthYear(
        month: Int,
        year: Int,
        callback: (List<List<BottomDetailDTO>>) -> Unit
    ) {
        val list = mutableListOf<MutableList<BottomDetailDTO>>()
        for (i in 0..montList[month]) {
            list.add(mutableListOf())
        }


        Firebase.firestore.collection("User").document(requireNotNull(auth.uid).toString()).get()
            .addOnCompleteListener { document ->
                if (document.isSuccessful) {
                    val userFollowList = document.result.get("followList") as List<String>

                    val startDate = "1/$month/$year"
                    val endDate =
                        "1/${if (month == 12) 1 else month + 1}/${if (month == 12) year + 1 else year}"
                    val formatter = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
                    val dayFormat = SimpleDateFormat("d", Locale.getDefault())
                    val sDate = formatter.parse(startDate) as Date
                    val eDate = formatter.parse(endDate) as Date

                    val cnt =  AtomicInteger(0)
                    for (id in userFollowList) {
                        Firebase.firestore.collection("Artist").document(id).get().addOnCompleteListener { artistInfo ->
                            if(artistInfo.isSuccessful){

                                Firebase.firestore.collection("Notice").whereEqualTo("artistId", id)
                                    .whereGreaterThanOrEqualTo("date", Timestamp(sDate))
                                    .whereLessThan("date", Timestamp(eDate)).get()
                                    .addOnCompleteListener { query ->
                                        if (query.isSuccessful) {
                                            for (item in query.result) {
                                                val noticeId = item.id
                                                val temp: Timestamp = item.get("date") as Timestamp
                                                val day =dayFormat.format(temp.toDate()).toInt()
                                                list[day].add(
                                                    BottomDetailDTO(
                                                        noticeId,
                                                        artistInfo.result.get("artistName") as String,
                                                        item.get("locationDescription") as String,
                                                        temp.toDate()
                                                    )
                                                )
                                            }
                                        }
                                        if(cnt.incrementAndGet() == userFollowList.size){
                                            callback(list)
                                        }
                                    }
                            }else{
                                if(cnt.incrementAndGet() == userFollowList.size){
                                    callback(list)
                                }
                            }
                        }

                    }
                    if(userFollowList.isEmpty()) {
                        callback(list)
                    }
                } else {
                    callback(list)
                }
            }


    }

}