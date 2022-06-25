package com.example.wetterbericht.view.fragment.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetterbericht.R
import com.example.wetterbericht.databinding.ActivityDetailWeatherBinding
import com.example.wetterbericht.model.remote.response.Foredata
import com.example.wetterbericht.model.remote.response.ForecastResponse
import com.example.wetterbericht.model.remote.service.WeatherResponse
import com.example.wetterbericht.view.adapter.ForecastAdapter
import com.example.wetterbericht.viewmodel.local.LocalViewModel
import com.example.wetterbericht.viewmodel.remote.WeatherViewModel
import com.example.wetterbericht.viewmodel.utils.obtainViewModel
import kotlin.math.round

class DetailWeatherActivity : AppCompatActivity() {

    private val weatherViewModel by viewModels<WeatherViewModel>()
    private lateinit var binding : ActivityDetailWeatherBinding


    private lateinit var roomViewModel: LocalViewModel

    private var forecastList = ArrayList<Foredata>()
    private lateinit var forecastAdapter: ForecastAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.main)
        roomViewModel = obtainViewModel(this)


        val lokasi = intent.getStringExtra("lokasi")
        searchCurrentWeather(lokasi.toString())

        weatherViewModel.isLoading.observe(this){
            binding.pgbarDetaweather.visibility = if (it) View.VISIBLE else View.GONE
        }



    }

    private fun searchCurrentWeather(city : String){
        weatherViewModel.apply {
            getWeatherSearch(city)
            weatherSearchRespon.observe(this@DetailWeatherActivity){ respon ->
                setWeatherCurrent(respon)
            }
            getWeatherForecast(city)
            forecastRespon.observe(this@DetailWeatherActivity){
                setWeatherForecast(it)
            }
        }
    }

    private fun setWeatherCurrent(data : WeatherResponse){
        binding.apply {
            data.apply {
                tvdetailWeatherLoc.text = name
                val temp = round(main.temp).toInt()
                tvdetailWeatherTemp.text = temp.toString() + " c"

                tvdetailWeatherDesc.text = weather[0].description

                tvdetailWeatherWind.text = wind.speed.toString() + "Km/h"
                tvdetailWeatherVisib.text = visibility.toString()+ " km"
                tvdetailWeatherPresuare.text = main.pressure.toString()+" Mbar"

                tvdetailWeatherCloud.text = "Clouds " + clouds.all.toString()
                tvdetailWeatherFeels.text =  "Feels like " +main.feelsLike.toString()


                val url = weather[0].icon
                val icon = "http://openweathermap.org/img/w/${url}.png"

                Glide.with(this@DetailWeatherActivity)
                    .asBitmap()
                    .load(icon)
                    .into(imgvWeather)
            }
        }
    }

    private fun setWeatherForecast(respon : ForecastResponse){
        val data = respon.list
        for (i in data.indices){
            val date = data[i].dtTxt
            val desc = data[i].weather[0].description
            val temp = data[i].main.temp.toString()

            val tempForecast = Foredata(
                date,
                desc,
                temp
            )

            forecastList.add(tempForecast)
            forecastAdapter = ForecastAdapter(forecastList.distinct())
            val recyclerView = binding.recviewForecast
            recyclerView.adapter = forecastAdapter
            recyclerView.layoutManager = LinearLayoutManager(this)
        }
    }



}