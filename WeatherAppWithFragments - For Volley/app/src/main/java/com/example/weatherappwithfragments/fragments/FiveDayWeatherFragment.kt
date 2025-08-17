package com.example.weatherappwithfragments.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import com.android.volley.VolleyError
import com.example.example.DailyForecasts
import com.example.example.ExampleJson2KtKotlin
import com.example.weatherappwithfragments.BuildConfig
import com.example.weatherappwithfragments.utils.NetworkUtil
import com.example.weatherappwithfragments.R
import com.example.weatherappwithfragments.adapters.WeatherAdapter
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class FiveDayWeatherFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var buttonSearch: MaterialButton
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var textViewEmptyState: TextView
    private lateinit var buttonRetry: MaterialButton
    private lateinit var editTextCity: AutoCompleteTextView
    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_five_day_weather, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewWeather)
        progressBar = view.findViewById(R.id.progressBar)
        buttonSearch = view.findViewById(R.id.buttonSearch)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        textViewEmptyState = view.findViewById(R.id.textViewEmptyState)
        buttonRetry = view.findViewById(R.id.buttonRetry)
        editTextCity = view.findViewById(R.id.editTextCity)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up search functionality
        setupSearchFunctionality()

        // Initial fetch
        fetchWeather()

        return view
    }

    private fun setupSearchFunctionality() {
        buttonSearch.setOnClickListener {
            val city = editTextCity.text.toString().trim()
            if (city.isNotEmpty()) {
                // For now, just fetch weather for the default location
                // You can implement city search functionality here
                fetchWeather()
            } else {
                Toast.makeText(requireContext(), "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        buttonRetry.setOnClickListener {
            fetchWeather()
        }

        // Add text watcher for search suggestions if needed
        editTextCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // You can implement city search suggestions here
            }
        })
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmptyState(show: Boolean, message: String = "No weather data available", showRetry: Boolean = false) {
        emptyStateLayout.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
        textViewEmptyState.text = message
        buttonRetry.visibility = if (showRetry) View.VISIBLE else View.GONE
    }

    private fun fetchWeather() {
        try {
            val baseUrl = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/1710150"
            val networkUtil = NetworkUtil(baseUrl)
            val url = networkUtil.buildURLForWeather()

            if (url == null) {
                Log.e("WeatherFrag", "Failed to build URL")
                Toast.makeText(requireContext(), "Error building request URL", Toast.LENGTH_SHORT).show()
                return
            }

            // Create request queue
            val requestQueue = Volley.newRequestQueue(requireContext())

            // Create JSON object request
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url.toString(),
                null,
                Response.Listener { response ->
                    try {
                        // Parse the JSON response
                        val weatherData = gson.fromJson(response.toString(), ExampleJson2KtKotlin::class.java)
                        val days = weatherData.DailyForecasts

                        // Update RecyclerView adapter on main thread
                        recyclerView.adapter = WeatherAdapter(days)

                        Log.d("WeatherFrag", "Successfully loaded ${days.size} weather forecasts")
                    } catch (e: Exception) {
                        Log.e("WeatherFrag", "Failed to parse JSON response", e)
                        Toast.makeText(requireContext(), "Error parsing weather data", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    Log.e("WeatherFrag", "Volley error: ${error.message}", error)

                    val errorMessage = when {
                        error.networkResponse?.statusCode == 401 -> "Invalid API key or unauthorized access"
                        error.networkResponse?.statusCode == 404 -> "Weather data not found"
                        error.networkResponse?.statusCode != null -> "Server error: ${error.networkResponse.statusCode}"
                        else -> "Network error: ${error.message ?: "Unknown error"}"
                    }

                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            )

            // Add request to queue
            requestQueue.add(jsonObjectRequest)

        } catch (e: Exception) {
            Log.e("WeatherFrag", "Error setting up weather request", e)
            Toast.makeText(requireContext(), "Error fetching weather: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}