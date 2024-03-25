package com.example.chatapplication.other

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseAuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    var idToken: String?=null

    // Example function to get the Firebase user and ID token
    fun getFirebaseUserAndToken() {
        // Get the current user
        val currentUser: FirebaseUser? = auth.currentUser

        // Check if the user is signed in
        if (currentUser != null) {
            // User is signed in
            currentUser.getIdToken(true)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Token retrieved successfully
                        idToken = task.result?.token
                        if (idToken != null) {
                            // Use the ID token as needed (e.g., send it to your server)
                            // ...
                        }
                    } else {
                        // Handle the error
                        val exception: Exception? = task.exception
                        // Handle the exception
                    }
                }
        } else {
            // User is not signed in
            // ...
        }
    }
}