package com.example.quickflow.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {
    // Replace these with your actual Supabase project credentials
    private const val SUPABASE_URL = "https://holmxyfslvcfevgzfwyd.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhvbG14eWZzbHZjZmV2Z3pmd3lkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTExOTQzNjcsImV4cCI6MjA2Njc3MDM2N30.6eN_0o1sxIZp7DWUmN4FqUWJ4p9WWNuLsNSGjReP0go"
    
    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Realtime)
        install(Storage)
    }
    
    val auth = client.auth
    val database = client.postgrest
    val realtime = client.realtime
    val storage = client.storage
} 