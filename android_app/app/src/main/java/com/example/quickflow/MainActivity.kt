package com.example.quickflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickflow.navigation.Screen
import com.example.quickflow.ui.theme.QuickFlowTheme
import com.example.quickflow.viewmodel.AuthViewModel
import com.example.quickflow.viewmodel.WorkflowViewModel
import com.example.quickflow.ui.LoginScreen
import com.example.quickflow.ui.DashboardScreen

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val workflowViewModel: WorkflowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickFlowTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        navController = navController,
                        authViewModel = authViewModel,
                        workflowViewModel = workflowViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    workflowViewModel: WorkflowViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { userId ->
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            val userId = authViewModel.currentUserId.collectAsState().value
            if (userId != null) {
                DashboardScreen(
                    userId = userId,
                    workflowViewModel = workflowViewModel,
                    onCreateWorkflow = {
                        // TODO: Navigate to CreateWorkflow screen
                    },
                    onEditWorkflow = { workflowId ->
                        // TODO: Navigate to EditWorkflow screen
                    },
                    onViewWorkflow = { workflowId ->
                        // TODO: Navigate to ViewWorkflow screen
                    }
                )
            } else {
                // If userId is null, navigate back to login
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            }
        }
        // Other screens will be added here
    }
}