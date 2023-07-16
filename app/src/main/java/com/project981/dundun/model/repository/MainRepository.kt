package com.project981.dundun.model.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project981.dundun.model.dto.firebase.ArtistDTO
import com.project981.dundun.model.dto.firebase.UserDTO

object MainRepository {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

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
                    if(isArtist) {
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
                    if(it.isSuccessful){
                        callback(Result.success(true))
                    }else{
                        callback(Result.failure(Exception("데이터 생성 실패")))
                        auth.currentUser?.delete()
                    }
                }
            }else{
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

}