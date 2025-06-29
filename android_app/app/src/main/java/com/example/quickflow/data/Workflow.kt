package com.example.quickflow.data

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Workflow(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val actions: List<String>,
    val createdAt: String,
    val updatedAt: String
) 