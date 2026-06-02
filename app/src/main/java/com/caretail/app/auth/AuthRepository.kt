package com.caretail.app.auth

import android.app.Activity
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    context: Context,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val googleAuthClient: GoogleAuthClient = GoogleAuthClient(context),
) {
    fun observeCurrentUser(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toAuthUser())
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.toAuthUser())
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithGoogle(activity: Activity): AuthResultMessage {
        return when (val result = googleAuthClient.getFirebaseCredential(activity)) {
            is GoogleAuthResult.Error -> AuthResultMessage.Error(result.message)
            is GoogleAuthResult.Success -> {
                try {
                    firebaseAuth.signInWithCredential(result.credential).await()
                    AuthResultMessage.Success
                } catch (error: Exception) {
                    AuthResultMessage.Error("Could not sign in. Please try again.")
                }
            }
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
        // TODO: Future cloud backup/sync should disconnect remote session state without deleting local Room data.
    }

    private fun FirebaseUser.toAuthUser(): AuthUser =
        AuthUser(
            uid = uid,
            displayName = displayName,
            email = email,
            photoUrl = photoUrl?.toString(),
        )
}

sealed interface AuthResultMessage {
    data object Success : AuthResultMessage
    data class Error(val message: String) : AuthResultMessage
}
