package com.example.weatherappwithfragments.utils

import android.net.Uri
import android.util.Log
import com.example.weatherappwithfragments.BuildConfig
import java.net.MalformedURLException
import java.net.URL

//utility or helper class with method to build the URL
class NetworkUtil(private val baseUrl: String) {
    private val PARAM_METRIC = "metric"
    private val METRIC_VALUE = "true"
    private val PARAM_API_KEY = "apikey"
    private val LOGGING_TAG = "URLWECREATED"

    fun buildURLForWeather(): URL? {
        // Debug logging to check API key
        val apiKey = BuildConfig.ACCUWEATHER_API_KEY
        Log.d(LOGGING_TAG, "API Key exists: ${apiKey.isNotEmpty()}")
        Log.d(LOGGING_TAG, "API Key length: ${apiKey.length}")
        Log.d(LOGGING_TAG, "API Key first 8 chars: ${if(apiKey.length >= 8) apiKey.substring(0, 8) else apiKey}")

        if (apiKey.isEmpty()) {
            Log.e(LOGGING_TAG, "API Key is empty! Check your BuildConfig setup.")
            return null
        }

        val buildUri = Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter(PARAM_API_KEY, apiKey)
            .appendQueryParameter(PARAM_METRIC, METRIC_VALUE)
            .build()

        Log.i(LOGGING_TAG, "Full URL with parameters: $buildUri")
        Log.i(LOGGING_TAG, "URL length: ${buildUri.toString().length}")

        return try {
            URL(buildUri.toString())
        } catch (e: MalformedURLException) {
            Log.e(LOGGING_TAG, "Malformed URL: ${e.message}", e)
            null
        }
    }

    // Helper method to get just the API key for testing
    fun getApiKey(): String {
        return BuildConfig.ACCUWEATHER_API_KEY
    }
}