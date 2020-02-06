package com.pixelart.notedock.domain.repository

import android.util.Patterns
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.reactivex.Completable

interface AuthRepository {
    fun login(email: String, password: String): Completable
    fun register(email: String, password: String): Completable
    fun sendPasswordResetEmail(email: String): Completable
    fun sendVerificationEmail(user: FirebaseUser): Completable
    fun reauthenticateUser(user: FirebaseUser, email: String, password: String): Completable
    fun changePassword(user: FirebaseUser, newPassword: String): Completable
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

    override fun reauthenticateUser(user: FirebaseUser, email: String, password: String): Completable {
        return Completable.create { emitter ->
            val credentials = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credentials)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }
    }

    override fun changePassword(user: FirebaseUser, newPassword: String): Completable {
        return Completable.create { emitter ->
            user.updatePassword(newPassword)
                .addOnSuccessListener { emitter.onComplete() }
                .addOnFailureListener { emitter.tryOnError(it) }
        }

    }
}

class InvalidEmailException : Throwable() {
    override val message: String?
        get() = "Text was not matched with `Patterns.EMAIL_ADDRESS`"
}