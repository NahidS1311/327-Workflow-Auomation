package com.example.quickflow.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.quickflow.data.Workflow
import com.example.quickflow.viewmodel.WorkflowViewModel

@Composable
fun DashboardScreen(
    userId: String,
    workflowViewModel: WorkflowViewModel,
    onCreateWorkflow: () -> Unit,
    onEditWorkflow: (String) -> Unit,
    onViewWorkflow: (String) -> Unit
) {
    val workflows by workflowViewModel.workflows.collectAsState()
    val isLoading by workflowViewModel.isLoading.collectAsState()
    val error by workflowViewModel.error.collectAsState()
    var workflowToDelete by remember { mutableStateOf<Workflow?>(null) }

    LaunchedEffect(userId) {
        workflowViewModel.loadWorkflows(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Your Workflows") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateWorkflow) {
                Text("+")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { workflowViewModel.loadWorkflows(userId) }) {
                            Text("Retry")
                        }
                    }
                }
                workflows.isEmpty() -> {
                    Text(
                        text = "No workflows yet. Click + to create one!",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(workflows) { workflow ->
                            WorkflowCard(
                                workflow = workflow,
                                onEdit = { onEditWorkflow(workflow.id) },
                                onView = { onViewWorkflow(workflow.id) },
                                onDelete = { workflowToDelete = workflow }
                            )
                        }
                    }
                }
            }
            if (workflowToDelete != null) {
                AlertDialog(
                    onDismissRequest = { workflowToDelete = null },
                    title = { Text("Delete Workflow") },
                    text = { Text("Are you sure you want to delete this workflow?") },
                    confirmButton = {
                        TextButton(onClick = {
                            workflowViewModel.deleteWorkflow(workflowToDelete!!.id)
                            workflowToDelete = null
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { workflowToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WorkflowCard(
    workflow: Workflow,
    onEdit: () -> Unit,
    onView: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = workflow.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "${workflow.actions.size} actions", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onView) {
                Icon(Icons.Default.Visibility, contentDescription = "View")
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
} 