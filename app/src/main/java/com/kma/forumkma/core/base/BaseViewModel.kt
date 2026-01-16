package com.kma.forumkma.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel với các chức năng common
 * - Loading state management
 * - Error handling
 * - Coroutine scope management
 */
abstract class BaseViewModel<State, Event>(
    initialState: State,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Update UI state
     */
    protected fun updateState(newState: State) {
        _uiState.value = newState
    }

    /**
     * Update partial state
     */
    protected fun updateState(block: State.() -> State) {
        _uiState.value = block(_uiState.value)
    }

    /**
     * Show loading
     */
    protected fun showLoading() {
        _isLoading.value = true
    }

    /**
     * Hide loading
     */
    protected fun hideLoading() {
        _isLoading.value = false
    }

    /**
     * Set error
     */
    protected fun setError(message: String?) {
        _error.value = message
    }

    /**
     * Clear error
     */
    protected fun clearError() {
        _error.value = null
    }

    /**
     * Launch coroutine với error handling
     */
    protected fun launchCatching(
        block: suspend () -> Unit,
        onError: (Throwable) -> Unit = { setError(it.message) }
    ) {
        viewModelScope.launch(dispatcher) {
            try {
                showLoading()
                block()
            } catch (e: Exception) {
                onError(e)
            } finally {
                hideLoading()
            }
        }
    }

    /**
     * Handle UI events
     */
    abstract fun onEvent(event: Event)
}
