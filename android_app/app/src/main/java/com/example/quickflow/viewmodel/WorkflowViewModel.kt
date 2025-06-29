package com.example.quickflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickflow.data.Workflow
import com.example.quickflow.data.WorkflowRepository
import com.example.quickflow.data.WorkflowCreateRequest
import com.example.quickflow.data.WorkflowUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkflowViewModel : ViewModel() {
    
    private val repository = WorkflowRepository()
    
    private val _workflows = MutableStateFlow<List<Workflow>>(emptyList())
    val workflows: StateFlow<List<Workflow>> = _workflows.asStateFlow()
    
    private val _currentWorkflow = MutableStateFlow<Workflow?>(null)
    val currentWorkflow: StateFlow<Workflow?> = _currentWorkflow.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadWorkflows(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val workflowList = repository.getWorkflows(userId)
                _workflows.value = workflowList
            } catch (e: Exception) {
                _error.value = "Failed to load workflows: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun createWorkflow(userId: String, name: String, actions: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = WorkflowCreateRequest(userId, name, actions)
                val newWorkflow = repository.createWorkflow(request)
                if (newWorkflow != null) {
                    _workflows.value = _workflows.value + newWorkflow
                } else {
                    _error.value = "Failed to create workflow"
                }
            } catch (e: Exception) {
                _error.value = "Failed to create workflow: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateWorkflow(workflowId: String, name: String, actions: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val request = WorkflowUpdateRequest(name, actions)
                val updatedWorkflow = repository.updateWorkflow(workflowId, request)
                if (updatedWorkflow != null) {
                    _workflows.value = _workflows.value.map { 
                        if (it.id == workflowId) updatedWorkflow else it 
                    }
                    _currentWorkflow.value = updatedWorkflow
                } else {
                    _error.value = "Failed to update workflow"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update workflow: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteWorkflow(workflowId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val success = repository.deleteWorkflow(workflowId)
                if (success) {
                    _workflows.value = _workflows.value.filter { it.id != workflowId }
                    if (_currentWorkflow.value?.id == workflowId) {
                        _currentWorkflow.value = null
                    }
                } else {
                    _error.value = "Failed to delete workflow"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete workflow: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadWorkflow(workflowId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val workflow = repository.getWorkflow(workflowId)
                _currentWorkflow.value = workflow
                if (workflow == null) {
                    _error.value = "Workflow not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to load workflow: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearCurrentWorkflow() {
        _currentWorkflow.value = null
    }
} 