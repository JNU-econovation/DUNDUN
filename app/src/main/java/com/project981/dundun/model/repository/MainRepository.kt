package com.project981.dundun.model.repository

import android.graphics.Bitmap
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project981.dundun.model.dto.BottomDetailDTO
import com.project981.dundun.model.dto.MarkerDTO
import com.project981.dundun.model.dto.NoticeChangeDTO
import com.project981.dundun.model.dto.NoticeCreateDTO
import com.project981.dundun.model.dto.NoticeDisplayDTO
import com.project981.dundun.model.dto.NoticeGetDTO
import com.project981.dundun.model.dto.ProfileTopDTO
import com.project981.dundun.model.dto.firebase.ArtistDTO
import com.project981.dundun.model.dto.firebase.NoticeDTO
import com.project981.dundun.model.dto.firebase.UserDTO
import java.io.ByteArrayOutputStream
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
                                name,
                                "안녕하세요! ${name}입니다!",
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


    fun getFollowerNoticeList(callback: (List<NoticeDisplayDTO>) -> Unit) {
        Firebase.firestore.collection("User").document(requireNotNull(auth.uid).toString()).get()
            .addOnCompleteListener { document ->
                if (document.isSuccessful) {
                    val userFollowList = document.result.get("followList") as List<String>
                    if(userFollowList.isEmpty()){
                        callback(listOf())
                        return@addOnCompleteListener
                    }
                    val tasks: MutableList<Task<DocumentSnapshot>> = ArrayList()
                    for (item in userFollowList) {
                        tasks.add(Firebase.firestore.collection("Artist").document(item).get())
                    }

                    Tasks.whenAllComplete(tasks).addOnCompleteListener {
                        val m = mutableMapOf<String, List<String>>()
                        for (i in userFollowList.indices) {
                            m[userFollowList[i]] = listOf(
                                (it.result[i].result as DocumentSnapshot).id,
                                (it.result[i].result as DocumentSnapshot).getString("artistName")!!,
                                (it.result[i].result as DocumentSnapshot).getString("profileImageUrl")!!
                            )
                        }

                        Firebase.firestore.collection("User").document(auth.uid!!).get()
                            .addOnCompleteListener { user ->
                                Firebase.firestore.collection("Notice")
                                    .whereIn("artistId", userFollowList)
                                    .orderBy("createTime", Query.Direction.DESCENDING).get()
                                    .addOnCompleteListener { taskQuery ->
                                        if (taskQuery.isSuccessful) {


                                            val list = mutableListOf<NoticeDisplayDTO>()
                                            for (item in taskQuery.result) {
                                                list.add(
                                                    NoticeDisplayDTO(
                                                        m[item.getString("artistId")!!]!![0],
                                                        item.id,
                                                        m[item.getString("artistId")!!]!![1],
                                                        m[item.getString("artistId")!!]!![2],
                                                        item.getTimestamp("createTime")!!.toDate(),
                                                        item.getString("noticeImage"),
                                                        item.getString("noticeContent")!!,
                                                        item.getString("locationDescription"),
                                                        item.getTimestamp("date")?.toDate(),
                                                        (user.result.get("likeList") as List<String>).contains(
                                                            item.id
                                                        )
                                                    )
                                                )
                                            }


                                            callback(list)
                                        }
                                    }
                            }
                    }


                }


            }
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
                                                            artistInfo.result.getString("artistName")!!,
                                                            item.getString("locationDescription"),
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

                val list = mutableListOf<MarkerDTO>()
                val atomicInteger = AtomicInteger(0)
                val matchingDoc = matchingDocs.filter {
                    val date = it.getTimestamp("date")?.toDate()
                    date != null && date.time in System.currentTimeMillis() - 86400000..System.currentTimeMillis() + 86400000
                }

                for (item in matchingDoc) {
                    val geo = item.getGeoPoint("geo")!!
                    Firebase.firestore.collection("Artist").document(item.getString("artistId")!!)
                        .get()
                        .addOnCompleteListener {
                            list.add(
                                MarkerDTO(
                                    mutableListOf(
                                        BottomDetailDTO(
                                            item.getString("artistId")!!,
                                            item.id,
                                            it.result.getString("artistName")!!,
                                            item.getString("locationDescription"),
                                            item.getTimestamp("date")!!.toDate()
                                        )
                                    ),
                                    geo.longitude,
                                    geo.latitude,
                                    1
                                )
                            )
                            if (atomicInteger.incrementAndGet() == matchingDoc.size) {

                                callback(Result.success(list))
                            }
                        }

                }


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
                if (it.isSuccessful) {
                    val list = it.result.get("followList") as List<String>

                    callback(list.contains(artistId))
                }
            }
    }

    fun getArtistTopInfo(artistId: String, callback: (ProfileTopDTO) -> Unit) {
        Firebase.firestore.collection("Artist").document(artistId).get()
            .addOnCompleteListener {
                callback(
                    ProfileTopDTO(
                        artistId,
                        it.result.getString("artistName")!!,
                        it.result.getString("profileImageUrl")!!,
                        it.result.getString("description")!!
                    )
                )
            }
    }

    fun getArtistNoticeList(artistId: String, callback: (List<NoticeDisplayDTO>) -> Unit) {

        Firebase.firestore.collection("Artist").document(artistId).get()
            .addOnCompleteListener { artist ->

                Firebase.firestore.collection("Notice")
                    .whereEqualTo("artistId", artistId)
                    .orderBy("createTime", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener { taskQuery ->
                        if (taskQuery.isSuccessful) {

                            Firebase.firestore.collection("User").document(auth.uid!!).get()
                                .addOnCompleteListener {

                                    val list = mutableListOf<NoticeDisplayDTO>()
                                    for (item in taskQuery.result) {
                                        list.add(
                                            NoticeDisplayDTO(
                                                artistId,
                                                item.id,
                                                artist.result.getString("artistName")!!,
                                                artist.result.getString("profileImageUrl")!!,
                                                item.getTimestamp("createTime")!!.toDate(),
                                                item.getString("noticeImage"),
                                                item.getString("noticeContent")!!,
                                                item.getString("locationDescription"),
                                                item.getTimestamp("date")?.toDate(),
                                                (it.result.get("likeList") as List<String>).contains(
                                                    item.id
                                                ),

                                            )
                                        )
                                    }


                                    callback(list)
                                }
                        }
                    }
            }

    }


    fun createNotice(info: NoticeCreateDTO, callback: (Boolean) -> Unit) {
        if (info.noticeImage != null) {
            val baos = ByteArrayOutputStream()
            info.noticeImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val url = "images/" + auth.uid + System.currentTimeMillis().toString() + ".jpg"
            val ref = Firebase.storage.reference.child(url)
            var uploadTask = ref.putBytes(data)
            uploadTask.addOnFailureListener {
                callback(false)
            }.addOnSuccessListener { taskSnapshot ->
                val downloadUri = ref.downloadUrl.addOnCompleteListener {

                    createNotice2(info, it.result.toString(), callback)
                }

            }
        } else {
            createNotice2(info, null, callback)
        }
    }

    private fun createNotice2(info: NoticeCreateDTO, url: String?, callback: (Boolean) -> Unit) {
        Firebase.firestore.collection("Notice").document().set(
            NoticeDTO(
                info.artistId,
                info.noticeContent,
                url,
                if (info.date == null) {
                    null
                } else {
                    Timestamp(info.date)
                },
                if (info.lat == null || info.lng == null) {
                    null
                } else {
                    GeoPoint(info.lat, info.lng)
                },
                if (info.lat == null || info.lng == null) {
                    null
                } else {
                    GeoFireUtils.getGeoHashForLocation(GeoLocation(info.lat, info.lng))
                },
                info.locationDescription,
                Timestamp.now(),
                Timestamp.now()
            )
        ).addOnCompleteListener {
            callback(true)
        }
    }

    fun updateNotice(info: NoticeChangeDTO, noticeId: String, callback: (Boolean) -> Unit) {
        if (info.contentImage != null) {
            val baos = ByteArrayOutputStream()
            info.contentImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val url = "images/" + auth.uid + System.currentTimeMillis().toString() + ".jpg"
            val ref = Firebase.storage.reference.child(url)
            var uploadTask = ref.putBytes(data)
            uploadTask.addOnFailureListener {
                callback(false)
            }.addOnSuccessListener { taskSnapshot ->
                val downloadUri = ref.downloadUrl.addOnCompleteListener {

                    updateNotice2(info, noticeId, it.result.toString(), callback)
                }

            }
        } else {
            updateNotice2(info, noticeId, null, callback)
        }
    }

    private fun updateNotice2(
        info: NoticeChangeDTO,
        noticeId: String,
        url: String?,
        callback: (Boolean) -> Unit
    ) {
        val m = mutableMapOf<String, Any?>(
            "noticeContent" to info.content,
            "updateTime" to Timestamp.now(),
            "geo" to if (info.latitude != null && info.longitude != null) {
                GeoLocation(info.latitude, info.longitude)
            } else {
                null
            },
            "geoHash" to if (info.latitude != null && info.longitude != null) {
                GeoFireUtils.getGeoHashForLocation(GeoLocation(info.latitude, info.longitude))
            } else {
                null
            },

            "locationDescription" to info.locationDescription,
            "date" to if (info.date != null) {
                Timestamp(info.date)
            } else {
                null
            }
        )


        Firebase.firestore.collection("Notice").document(noticeId).update(m)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    fun getArtistTopByPrefix(prefix: String, callback: (List<ProfileTopDTO>) -> Unit) {
        Firebase.firestore.collection("Artist").whereGreaterThanOrEqualTo("artistName", prefix)
            .whereLessThanOrEqualTo("artistName", prefix + "힣").orderBy("artistName").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val list = mutableListOf<ProfileTopDTO>()
                    for (item in it.result.documents) {
                        if(item.id == "delete") continue
                        list.add(
                            ProfileTopDTO(
                                item.id,
                                item.getString("artistName")!!,
                                item.getString("profileImageUrl")!!,
                                item.getString("description")!!
                            )
                        )
                    }

                    callback(list)
                }
            }
    }

    fun changeNoticeLike(noticeId: String, isLike: Boolean, callback: (Boolean) -> Unit) {
        if (isLike) {
            Firebase.firestore.collection("User").document(auth.uid.toString())
                .update("likeList", FieldValue.arrayUnion(noticeId)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
        } else {
            Firebase.firestore.collection("User").document(auth.uid.toString())
                .update("likeList", FieldValue.arrayRemove(noticeId)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        callback(false)
                    } else {
                        callback(true)
                    }
                }
        }
    }

    fun getNotice(noticeId: String, callback: (NoticeGetDTO) -> Unit) {
        Firebase.firestore.collection("Notice").document(noticeId).get()
            .addOnCompleteListener {

                val geoPoint = it.result.getGeoPoint("geo")

                callback(NoticeGetDTO(
                    it.result.getString("noticeImage"),
                    it.result.getString("noticeContent")!!,
                    it.result.getString("locationDescription"),
                    geoPoint?.latitude,
                    geoPoint?.longitude,
                    it.result.getTimestamp("date")?.toDate()
                ))
            }
    }

    fun deleteNotice(noticeId: String, callback: (Boolean) -> Unit){
        val m = mutableMapOf<String, Any?>(
            "noticeContent" to "삭제된 공지 입니다.",
            "updateTime" to Timestamp.now(),
            "geo" to null,
            "geoHash" to null,
            "locationDescription" to null,
            "noticeImage" to null,
            "date" to null,
            "artistId" to "delete"
        )


        Firebase.firestore.collection("Notice").document(noticeId).update(m)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }
}