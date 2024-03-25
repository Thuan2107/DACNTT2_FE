package com.example.chatapplication.other

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckProvider
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.AppCheckToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class CustomProvider {
    class YourCustomAppCheckToken(
        private val token: String,
        private val expiration: Long,
    ) : AppCheckToken() {
        override fun getToken(): String = token
        override fun getExpireTimeMillis(): Long = expiration
    }

    class YourCustomAppCheckProvider(firebaseApp: FirebaseApp) : AppCheckProvider {
        override fun getToken(): Task<AppCheckToken> {
            // Use Firebase Authentication to get the user token
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser: FirebaseUser? = firebaseAuth.currentUser

            return if (currentUser != null) {
                // If the user is authenticated, get the Firebase ID token
                currentUser.getIdToken(true)
                    .continueWithTask { task ->
                        if (task.isSuccessful) {
                            // Token obtained successfully
                            val idToken = task.result?.token
                            val expirationFromServer = 0L // Set your desired expiration time

                            // Create AppCheckToken object.
                            val appCheckToken: AppCheckToken =
                                YourCustomAppCheckToken(idToken ?: "", expirationFromServer)
                            Tasks.forResult(appCheckToken)
                        } else {
                            // Handle error
                            // You might want to log an error, throw an exception, or handle it in another way
                            throw task.exception ?: Exception("Unknown error occurred")
                        }
                    }
            } else {
                // Handle the case where the user is not authenticated
                // You might want to log an error, throw an exception, or handle it in another way
                Tasks.forException(Exception("User not authenticated"))
            }
        }
    }
    // [END appcheck_custom_provider]

    // [START appcheck_custom_provider_factory]
    class YourCustomAppCheckProviderFactory : AppCheckProviderFactory {
        override fun create(firebaseApp: FirebaseApp): AppCheckProvider {
            // Create and return an AppCheckProvider object.
            return YourCustomAppCheckProvider(firebaseApp)
        }
    }
}