package com.garyfimo.gogym.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garyfimo.gogym.home.api.HomeApi
import com.garyfimo.gogym.home.model.WeeklyActivityStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeDashboardState(
    val stats: WeeklyActivityStats? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeDashboardViewModel(
    private val homeApi: HomeApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeDashboardState())
    val uiState: StateFlow<HomeDashboardState> = _uiState.asStateFlow()

    init {
        loadWeeklyStats()
    }

    fun loadWeeklyStats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val weeklyStats = homeApi.getWeeklyActivityStats()
                _uiState.update { it.copy(stats = weeklyStats, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load stats") }
            }
        }
    }
}
