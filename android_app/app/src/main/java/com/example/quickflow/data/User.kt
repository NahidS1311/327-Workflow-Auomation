package com.example.quickflow.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val googleAuthToken: String? = null
) 