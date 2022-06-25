package com.example.wetterbericht.view.fragment.weather


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wetterbericht.R
import com.example.wetterbericht.databinding.FragmentWeatherBinding
import com.example.wetterbericht.model.remote.response.ForecastResponse
import com.example.wetterbericht.model.remote.response.Foredata
import com.example.wetterbericht.model.remote.service.WeatherResponse
import com.example.wetterbericht.view.adapter.ForecastAdapter
import com.example.wetterbericht.view.adapter.WeatherRvFavoriteAdapter
import com.example.wetterbericht.viewmodel.local.LocalViewModel
import com.example.wetterbericht.viewmodel.remote.WeatherViewModel
import com.example.wetterbericht.viewmodel.utils.obtainViewModel
import kotlin.math.round


class WeatherFragment : Fragment(){

    private lateinit var binding : FragmentWeatherBinding

    private val weatherViewModel by viewModels<WeatherViewModel>()
    private lateinit var roomViewModel : LocalViewModel


    private lateinit var forecastAdapter: ForecastAdapter

    private var forecastList = ArrayList<Foredata>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbarWeather)
        roomViewModel = obtainViewModel(requireActivity())

        favoriteCity()

        return binding.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.weather_toolbar,menu)

        val searchView = menu.findItem(R.id.search_weather).actionView as SearchView
        searchView.apply {
            queryHint = "Enter Location"

            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchCurrentWeather(query ?: "")
                    locationChecker(query ?: "")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })
        }
    }

    private fun favoriteCity(){
        roomViewModel.readWeatherLocal()
        roomViewModel.responseWeatherLocal.observe(viewLifecycleOwner) { response ->
            if (response.isNotEmpty()){
                searchCurrentWeather(response[0].loc)
            }
        }
    }


    private fun searchCurrentWeather(city : String){
        weatherViewModel.apply {
            getWeatherSearch(city)
            weatherSearchRespon.observe(viewLifecycleOwner){
                setWeatherCard(it)
                forecastList.clear()
            }
            getWeatherForecast(city)
            forecastRespon.observe(viewLifecycleOwner){
                setWeatherForecast(it)
            }
        }
    }

    private fun locationChecker(location : String){
        roomViewModel.searchLocation(location).observe(viewLifecycleOwner){
            if (it.isNullOrEmpty()){
                binding.btnSetLocation.visibility = View.VISIBLE
            }else{
                binding.btnSetLocation.visibility = View.GONE
            }
        }
    }

    private fun setWeatherCard(data : WeatherResponse){
        binding.apply {
            data.apply {
                tvweatherLoc.text = name
                tvweatherCon.text = weather[0].description

                val temp = round(main.temp).toInt()
                val visibility = visibility / 1000

                tvweatherTemp.text = temp.toString() + " c"

                tvweatherClouds.text = "Clouds  ${clouds.all} %"
                tvweatherFeels.text =  "Feels like ${main.feelsLike} C "
                tvweatherWindspeed.text = "Wind Speed ${wind.speed} km/h"
                tvweatherVisibilty.text = "Visibilty $visibility Km"

                val url = weather[0].icon
                val icon = "http://openweathermap.org/img/w/${url}.png"

                Glide.with(requireContext())
                    .asBitmap()
                    .load(icon)
                    .into(imgIconweather)

                tvdetailWeatherLoc.setOnClickListener {
                    val intent = Intent(requireContext(), DetailWeatherActivity::class.java)
                    intent.putExtra("lokasi",name)
                    startActivity(intent)
                }

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
            val recyclerView = binding.weatherForecast
            recyclerView.adapter = forecastAdapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}