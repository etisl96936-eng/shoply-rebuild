package com.shoply.app.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun login(email: String, password: String): Result<UserSession> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(loadCurrentUserSession())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, displayName: String = ""): Result<UserSession> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: error("לא התקבל משתמש לאחר הרשמה")

            createOrUpdateGoogleUser(
                uid = user.uid,
                email = user.email.orEmpty(),
                displayName = displayName
            )

            Result.success(loadCurrentUserSession())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<UserSession> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: error("לא התקבל משתמש מ-Google")

            createOrUpdateGoogleUser(
                uid = user.uid,
                email = user.email.orEmpty(),
                displayName = user.displayName.orEmpty()
            )

            Result.success(loadCurrentUserSession())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadCurrentUserSession(): UserSession {
        val user = auth.currentUser ?: return UserSession()
        val doc = db.collection("users").document(user.uid).get().await()

        return UserSession(
            uid = user.uid,
            email = user.email.orEmpty(),
            displayName = doc.getString("displayName").orEmpty(),
            role = doc.getString("role") ?: "user"
        )
    }

    private suspend fun createOrUpdateGoogleUser(uid: String, email: String, displayName: String) {
        val userRef = db.collection("users").document(uid)
        val snapshot = userRef.get().await()

        if (snapshot.exists()) {
            userRef.update(
                mapOf(
                    "email" to email
                )
            ).await()
        } else {
            userRef.set(
                mapOf(
                    "email" to email,
                    "displayName" to displayName,
                    "role" to "user"
                )
            ).await()
        }
    }

    fun logout() {
        auth.signOut()
    }
}
