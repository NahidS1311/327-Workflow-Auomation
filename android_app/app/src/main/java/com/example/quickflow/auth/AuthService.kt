package com.example.quickflow.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.quickflow.data.SupabaseConfig
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Google
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthService(private val context: Context) {
    
    private lateinit var googleSignInClient: GoogleSignInClient
    
    init {
        setupGoogleSignIn()
    }
    
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID") // Replace with your web client ID
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    suspend fun handleSignInResult(task: Task<GoogleSignInAccount>): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                
                if (idToken != null) {
                    // Sign in to Supabase with Google token
                    val session = SupabaseConfig.auth.signInWith(Google) {
                        idToken = idToken
                    }
                    
                    AuthResult.Success(session.user?.id ?: "")
                } else {
                    AuthResult.Error("Failed to get ID token")
                }
            } catch (e: ApiException) {
                AuthResult.Error("Google sign in failed: ${e.statusCode}")
            } catch (e: Exception) {
                AuthResult.Error("Authentication failed: ${e.message}")
            }
        }
    }
    
    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            try {
                SupabaseConfig.auth.signOut()
                googleSignInClient.signOut()
            } catch (e: Exception) {
                // Handle sign out error
            }
        }
    }
    
    suspend fun getCurrentUser(): String? {
        return withContext(Dispatchers.IO) {
            try {
                SupabaseConfig.auth.currentSessionOrNull()?.user?.id
            } catch (e: Exception) {
                null
            }
        }
    }
    
    suspend fun isUserLoggedIn(): Boolean {
        return getCurrentUser() != null
    }
}

sealed class AuthResult {
    data class Success(val userId: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
} 