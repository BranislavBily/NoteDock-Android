package com.pixelart.notedock.domain.repository

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable

interface AuthRepository {
    fun login(email: String, password: String): Completable
}

class AuthRepositoryImpl(private val auth: FirebaseAuth): AuthRepository {
    override fun login(email: String, password: String): Completable {
        return Completable.create { emitter ->
               auth.signInWithEmailAndPassword(email, password)
                   .addOnSuccessListener {
                       emitter.onComplete()
                   }
                   .addOnFailureListener { error ->
                       emitter.tryOnError(error)
                   }
        }
    }
}