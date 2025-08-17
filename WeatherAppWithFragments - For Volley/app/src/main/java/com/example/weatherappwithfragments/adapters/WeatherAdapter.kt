package com.example.weatherappwithfragments.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.example.DailyForecasts
import com.example.weatherappwithfragments.R

class WeatherAdapter(private var forecastList: List<DailyForecasts>) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun getItemCount() = forecastList.size

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = forecastList[position]
        holder.date.text = item.Date?.take(10)
        holder.minTemp.text = "Min: ${item.Temperature?.Minimum?.Value}°C"
        holder.maxTemp.text = "Max: ${item.Temperature?.Maximum?.Value}°C"
        holder.icon.setImageResource(R.drawable.sunny)
    }

    inner class WeatherViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val icon: ImageView = item.findViewById(R.id.imageViewIcon)
        val date: TextView = item.findViewById(R.id.textViewDate)
        val minTemp: TextView = item.findViewById(R.id.textViewMin)
        val maxTemp: TextView = item.findViewById(R.id.textViewMax)
    }
}