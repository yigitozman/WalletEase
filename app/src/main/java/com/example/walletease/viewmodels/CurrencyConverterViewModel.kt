package com.example.walletease.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletease.api.CurrencyApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {

    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val exchangeRates: StateFlow<Map<String, Double>> get() = _exchangeRates

    private val apiService = CurrencyApiService.create()

    fun fetchLatestRates(base: String) {
        val apiKey = "YOUR_API_KEY"
        viewModelScope.launch {
            try {
                val response = apiService.getLatestRates(apiKey, base)
                _exchangeRates.value = response.rates
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}