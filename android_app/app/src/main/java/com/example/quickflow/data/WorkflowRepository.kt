package com.example.quickflow.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class WorkflowCreateRequest(
    val userId: String,
    val name: String,
    val actions: List<String>
)

@Serializable
data class WorkflowUpdateRequest(
    val name: String,
    val actions: List<String>
)

class WorkflowRepository {
    
    suspend fun getWorkflows(userId: String): List<Workflow> = withContext(Dispatchers.IO) {
        try {
            SupabaseConfig.database
                .from("workflows")
                .select(columns = Columns.all()) {
                    eq("user_id", userId)
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<Workflow>()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun createWorkflow(request: WorkflowCreateRequest): Workflow? = withContext(Dispatchers.IO) {
        try {
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val workflow = Workflow(
                userId = request.userId,
                name = request.name,
                actions = request.actions,
                createdAt = now,
                updatedAt = now
            )
            
            SupabaseConfig.database
                .from("workflows")
                .insert(workflow)
                .decodeSingle<Workflow>()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun updateWorkflow(workflowId: String, request: WorkflowUpdateRequest): Workflow? = withContext(Dispatchers.IO) {
        try {
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            val updateData = mapOf(
                "name" to request.name,
                "actions" to request.actions,
                "updated_at" to now
            )
            
            SupabaseConfig.database
                .from("workflows")
                .update(updateData) {
                    eq("id", workflowId)
                }
                .decodeSingle<Workflow>()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun deleteWorkflow(workflowId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            SupabaseConfig.database
                .from("workflows")
                .delete {
                    eq("id", workflowId)
                }
            true
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun getWorkflow(workflowId: String): Workflow? = withContext(Dispatchers.IO) {
        try {
            SupabaseConfig.database
                .from("workflows")
                .select(columns = Columns.all()) {
                    eq("id", workflowId)
                }
                .decodeSingle<Workflow>()
        } catch (e: Exception) {
            null
        }
    }
} 