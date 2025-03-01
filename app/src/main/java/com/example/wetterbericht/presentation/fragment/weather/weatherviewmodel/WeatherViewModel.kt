package com.example.wetterbericht.presentation.fragment.weather.weatherviewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wetterbericht.data.local.entity.weather.WeatherLocal
import com.example.wetterbericht.data.remote.openweather.forecast.ForecastResponse
import com.example.wetterbericht.data.remote.openweather.weather.WeatherResponse
import com.example.wetterbericht.domain.localusecase.weather.WeatherUseCase
import com.example.wetterbericht.domain.remoteusecase.RemoteUseCase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel(
    private val remoteUseCase: RemoteUseCase,
    private val weatherUseCase: WeatherUseCase
) : ViewModel() {

    val weatherSearchResponse : MutableLiveData<WeatherResponse> = MutableLiveData()
    val forecastResponse : MutableLiveData<ForecastResponse> = MutableLiveData()

    val isLoading : MutableLiveData<Boolean> = MutableLiveData()
    val status : MutableLiveData<String> = MutableLiveData()

    fun getWeatherSearch(loc : String){
        isLoading.value = true
        remoteUseCase.getWeatherBySearch(loc).enqueue(object : Callback<WeatherResponse>{
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
               if (response.isSuccessful){
                   isLoading.value = false
                   weatherSearchResponse.value = response.body()
               }else{
                   isLoading.value = false
                   status.value = response.message()
                   Log.d(tag,response.message())
               }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                isLoading.value = false
                status.value = t.message.toString()
                Log.d(tag,t.message.toString())
            }
        })
    }


    fun getWeatherForecast(loc: Any){
        isLoading.value = true
        remoteUseCase.getWeatherForecast(loc).enqueue(object : Callback<ForecastResponse>{
            override fun onResponse(
                call: Call<ForecastResponse>,
                response: Response<ForecastResponse>
            ) {
                if (response.isSuccessful){
                    isLoading.value = false
                    forecastResponse.value = response.body()
                }else{
                    isLoading.value = false
                    Log.d(tag,response.message())
                }
            }

            override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                Log.d(tag,t.message.toString())
            }
        })
    }

    //weather
    fun readWeatherLocal(): LiveData<List<WeatherLocal>> =
        weatherUseCase.readWeatherLocal()

    fun insertWeatherLocal(data : WeatherLocal) =
        weatherUseCase.insertWeatherLocal(data)

    fun searchLocation(name: String): LiveData<List<WeatherLocal>> =
        weatherUseCase.searchWeatherLocal(name)

    companion object{
        const val tag = "weatherviewmodel"
    }

}