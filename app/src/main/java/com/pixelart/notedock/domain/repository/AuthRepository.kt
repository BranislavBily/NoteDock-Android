package com.pixelart.notedock.domain.repository

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Completable

interface AuthRepository {
    fun login(email: String, password: String): Completable
    fun register(email: String, password: String): Completable
    fun sendPasswordResetEmail(email: String): Completable
    fun sendVerificationEmail(user: FirebaseUser): Completable
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

    override fun sendPasswordResetEmail(email: String): Completable {
        return Completable.create { emitter ->
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener { error ->
                    emitter.tryOnError(error)
                }
        }
    }

    override fun sendVerificationEmail(user: FirebaseUser): Completable {
        return Completable.create { emitter ->
            user.sendEmailVerification()
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