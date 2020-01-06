package com.pixelart.notedock.domain.repository

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable

interface AuthRepository {
    fun login(email: String, password: String): Completable
    fun register(email: String, password: String): Completable
}

class AuthRepositoryImpl(private val auth: FirebaseAuth): AuthRepository {
    override fun login(email: String, password: String): Completable {
        return Completable.create { emitter ->
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emitter.tryOnError(InvalidEmailException())
                return@create
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener { error ->
                    emitter.tryOnError(error)
                }
        }
    }

    override fun register(email: String, password: String): Completable {
        return Completable.create { emitter ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener { error ->
                    emitter.tryOnError(error)
                }
        }
    }
}

class InvalidEmailException : Throwable() {
    override val message: String?
        get() = "Text was not matched with `Patterns.EMAIL_ADDRESS`"
}