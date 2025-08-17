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
        val buildUri = Uri.parse(baseUrl).buildUpon()
            .appendQueryParameter(PARAM_API_KEY, BuildConfig.ACCUWEATHER_API_KEY)
            .appendQueryParameter(PARAM_METRIC, METRIC_VALUE)
            .build()

        Log.i(LOGGING_TAG, "buildUriForWeather: $buildUri")
        return try {
            URL(buildUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }
}
