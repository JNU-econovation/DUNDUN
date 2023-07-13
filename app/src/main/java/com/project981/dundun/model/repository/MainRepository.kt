package com.project981.dundun.model.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    fun createUser(email: String, pw: String, name: String, callback: (Result<Boolean>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Firebase.firestore
                    .collection("Email")
                    .document(email)
                    .set(
                        mapOf("1" to "1")
                    )

                Firebase.firestore
                    .collection("User")
                    .document(auth.currentUser?.uid.toString())
                    .set(
                        UserDTO(
                            listOf(),
                            listOf(),
                            name,
                            Timestamp.now(),
                            Timestamp.now()
                        )
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            callback(Result.success(true))
                        } else {
                            auth.currentUser?.delete()
                            callback(Result.failure(Exception("계정 생성에 성공했지만 관련 데이터 생성에 실패")))
                        }
                    }
            } else {
                callback(Result.failure(Exception("계정 생성에 실패")))
            }
        }
    }

    fun checkSignIn(email: String, pw: String, callback: (Result<Boolean>) -> Unit) {
        auth.signInWithEmailAndPassword(email,pw).addOnCompleteListener {
            if(it.isSuccessful){
                callback(Result.success(true))
            }else{
                callback(Result.failure(Exception("로그인 실패")))
            }
        }
    }

}