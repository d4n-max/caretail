package com.caretail.app.auth

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthClient(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val credentialManager = CredentialManager.create(appContext)

    suspend fun getFirebaseCredential(activity: Activity): GoogleAuthResult {
        val webClientId = defaultWebClientId()

        if (webClientId.isNullOrBlank()) {
            return GoogleAuthResult.Error("Google sign-in is not configured yet.")
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val response = credentialManager.getCredential(
                context = activity,
                request = request,
            )
            val credential = response.credential
            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                GoogleAuthResult.Success(GoogleAuthProvider.getCredential(googleCredential.idToken, null))
            } else {
                GoogleAuthResult.Error("Could not sign in. Please try again.")
            }
        } catch (error: GetCredentialCancellationException) {
            GoogleAuthResult.Error("Sign-in cancelled.")
        } catch (error: NoCredentialException) {
            GoogleAuthResult.Error("No Google account is available for sign-in.")
        } catch (error: GoogleIdTokenParsingException) {
            GoogleAuthResult.Error("Could not sign in. Please try again.")
        } catch (error: GetCredentialException) {
            GoogleAuthResult.Error("Could not sign in. Please try again.")
        }
    }

    private fun defaultWebClientId(): String? {
        val resourceId = appContext.resources.getIdentifier(
            "default_web_client_id",
            "string",
            appContext.packageName,
        )
        return resourceId.takeIf { it != 0 }?.let(appContext::getString)
    }
}

sealed interface GoogleAuthResult {
    data class Success(val credential: AuthCredential) : GoogleAuthResult
    data class Error(val message: String) : GoogleAuthResult
}
