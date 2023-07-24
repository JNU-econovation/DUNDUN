package com.project981.dundun.model.repository

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project981.dundun.model.dto.BottomDetailDTO
import com.project981.dundun.model.dto.MarkerDTO
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

                    val cnt = AtomicInteger(0)
                    for (id in userFollowList) {
                        Firebase.firestore.collection("Artist").document(id).get()
                            .addOnCompleteListener { artistInfo ->
                                if (artistInfo.isSuccessful) {

                                    Firebase.firestore.collection("Notice")
                                        .whereEqualTo("artistId", id)
                                        .whereGreaterThanOrEqualTo("date", Timestamp(sDate))
                                        .whereLessThan("date", Timestamp(eDate)).get()
                                        .addOnCompleteListener { query ->
                                            if (query.isSuccessful) {
                                                for (item in query.result) {
                                                    val noticeId = item.id
                                                    val temp: Timestamp =
                                                        item.get("date") as Timestamp
                                                    val day =
                                                        dayFormat.format(temp.toDate()).toInt()
                                                    list[day].add(
                                                        BottomDetailDTO(
                                                            artistInfo.result.id,
                                                            noticeId,
                                                            artistInfo.result.get("artistName") as String,
                                                            item.get("locationDescription") as String,
                                                            temp.toDate()
                                                        )
                                                    )
                                                }
                                            }
                                            if (cnt.incrementAndGet() == userFollowList.size) {
                                                callback(list)
                                            }
                                        }
                                } else {
                                    if (cnt.incrementAndGet() == userFollowList.size) {
                                        callback(list)
                                    }
                                }
                            }

                    }
                    if (userFollowList.isEmpty()) {
                        callback(list)
                    }
                } else {
                    callback(list)
                }
            }


    }

    fun getMarkerListByDistanceAndGeo(
        distance: Double,
        latitude: Double,
        longitude: Double,
        callback: (Result<List<MarkerDTO>>) -> Unit
    ) {


        val bounds = GeoFireUtils.getGeoHashQueryBounds(GeoLocation(latitude, longitude), distance)
        val tasks: MutableList<Task<QuerySnapshot>> = ArrayList()
        for (b in bounds) {
            val q = Firebase.firestore.collection("Notice")
                .whereGreaterThan("date", Timestamp(Date(System.currentTimeMillis() - 86400000)))
                .whereLessThan("date", Timestamp(Date(System.currentTimeMillis())))
                .orderBy("date")
                .orderBy("geoHash")
                .startAt(b.startHash)
                .endAt(b.endHash)
            tasks.add(q.get())
        }


        Tasks.whenAllComplete(tasks)
            .addOnCompleteListener {
                val matchingDocs: MutableList<DocumentSnapshot> = ArrayList()
                for (task in tasks) {
                    val snap = task.result
                    for (doc in snap!!.documents) {
                        val geo = doc.getGeoPoint("geo")!!

                        val docLocation = GeoLocation(geo.latitude, geo.longitude)
                        val distanceInM = GeoFireUtils.getDistanceBetween(
                            docLocation,
                            GeoLocation(latitude, longitude)
                        )
                        if (distanceInM <= distance) {
                            matchingDocs.add(doc)
                        }
                    }
                }

                val nameTask: MutableList<Task<DocumentSnapshot>> = ArrayList()
                for (doc in matchingDocs) {
                    val q = Firebase.firestore.collection("Artist")
                        .document(doc.getString("artistId")!!)
                    nameTask.add(q.get())
                }
                val list = mutableListOf<MarkerDTO>()
                Tasks.whenAllComplete(nameTask)
                    .addOnCompleteListener {
                        for (i in matchingDocs.indices) {
                            val geo = matchingDocs[i].getGeoPoint("geo")!!
                            list.add(
                                MarkerDTO(
                                    mutableListOf(
                                        BottomDetailDTO(
                                            matchingDocs[i].getString("artistId")!!,
                                            matchingDocs[i].id,
                                            nameTask[i].result.getString("artistName")!!,
                                            matchingDocs[i].getString("locationDescription")!!,
                                            matchingDocs[i].getTimestamp("date")!!.toDate()
                                        )
                                    ),
                                    geo.longitude,
                                    geo.latitude,
                                    1
                                )
                            )
                        }
                    }


                callback(Result.success(list))
            }
    }


    fun getArtistId(callback: (String?) -> Unit) {
        Firebase.firestore.collection("Artist").whereEqualTo("uid", auth.uid).get()
            .addOnCompleteListener {
                if (it.isSuccessful && it.result != null) {
                    callback(it.result.documents[0].id)
                } else {
                    callback(null)
                }
            }
    }

    fun changeFollowArtist(artistId: String, isFollow: Boolean, callback: (Boolean) -> Unit) {
        if (isFollow) {
            Firebase.firestore.collection("User").document(auth.uid.toString())
                .update("followList", FieldValue.arrayRemove(artistId)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(false)
                    }
                }
        } else {
            Firebase.firestore.collection("User").document(auth.uid.toString())
                .update("followList", FieldValue.arrayUnion(artistId)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(true)
                    }
                }
        }
    }

    fun getArtistIsFollow(artistId: String, callback: (Boolean) -> Unit) {
        Firebase.firestore.collection("User").document(auth.uid.toString()).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val list = it.result.get("followList") as List<String>

                    callback(list.contains(artistId))
                }
            }
    }

}