package org.videolan.vlc.gui.onboarding

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException

/**
 * Google sign-in through Android Credential Manager.
 * serverClientId must be the WEB client ID from Google Cloud Console.
 */
object GoogleSignInHelper {

    sealed class SignInResult {
        data class Success(val displayName: String?, val email: String?) : SignInResult()
        data class Failure(val message: String) : SignInResult()
    }

    suspend fun signIn(activity: Activity, serverClientId: String): SignInResult {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val response = CredentialManager.create(activity).getCredential(activity, request)
            val credential = response.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val google = GoogleIdTokenCredential.createFrom(credential.data)
                SignInResult.Success(google.displayName, google.id)
            } else {
                SignInResult.Failure("Unexpected credential type")
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            SignInResult.Failure(e.message ?: e.javaClass.simpleName)
        }
    }
}
