package com.example.walletease.screens.CurrencyConverterScreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletease.api.CurrencyApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel : ViewModel() {

    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val exchangeRates: StateFlow<Map<String, Double>> get() = _exchangeRates

    private val _conversionResult = MutableStateFlow<String?>(null)
    val conversionResult: StateFlow<String?> get() = _conversionResult

    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> get() = _isFetching

    private val apiService = CurrencyApiService.create()

    private fun fetchLatestRates(base: String) {
        val apiKey = "YOUR_API_KEY"
        viewModelScope.launch {
            _isFetching.value = true
            try {
                val response = apiService.getLatestRates(apiKey, base)
                _exchangeRates.value = response.rates
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isFetching.value = false
            }
        }
    }

    fun convertCurrency(amount: Double, baseCurrency: String, targetCurrency: String) {
        viewModelScope.launch {
            _conversionResult.value = "Fetching conversion rate..."
            fetchLatestRates(baseCurrency)
            _isFetching.collect { isFetching ->
                if (!isFetching) {
                    val rate = _exchangeRates.value[targetCurrency]
                    if (rate != null) {
                        val convertedAmount = amount * rate
                        _conversionResult.value = "$amount $baseCurrency\n$convertedAmount $targetCurrency"
                    } else {
                        _conversionResult.value = "Conversion rate not available"
                    }
                }
            }
        }
    }
    fun clearResult() {
        _conversionResult.value = null
    }
}