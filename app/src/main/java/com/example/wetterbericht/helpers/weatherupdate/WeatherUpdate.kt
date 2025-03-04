package com.example.wetterbericht.helpers.weatherupdate

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.wetterbericht.presentation.activity.mainactivity.MainActivity
import com.example.wetterbericht.R
import com.example.wetterbericht.injection.Injection
import com.example.wetterbericht.data.local.entity.weather.WeatherLocal
import com.example.wetterbericht.data.remote.openweather.weather.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.round

class WeatherUpdate : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Executors.newSingleThreadExecutor().execute {
            val weatherDao = Injection.providedUseCase(context)
            val cityName = weatherDao.getWeatherCityName()
            if (cityName.loc.isNotEmpty()){
                updateWeatherData(context,cityName.loc)
            }
        }
    }

    fun setDailyUpdate(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WeatherUpdate::class.java)

        //start reminder from 5 am
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            UPDATE_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1800000,
            pendingIntent
        )
    }

    private fun updateWeatherData(context: Context,cityName: String){
        val getWeather = Injection.provideWeatherUseCase().getWeatherBySearch(cityName)
        getWeather.enqueue(object : Callback<WeatherResponse>{
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                if (response.isSuccessful){
                    response.body()?.let { showWeatherNotification(context, it) }
                }else{
                    Log.d("Weather update",response.message())
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.d("Weather update",t.message.toString())
            }
        })
    }

    private fun showWeatherNotification(context: Context,data : WeatherResponse){
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainActivity::class.java)
        val temperature = round(data.main.temp).toInt()
        val iconUrl = "http://openweathermap.org/img/w/${data.weather[0].icon}.png"
        val insertData = WeatherLocal(
            0,
            data.name,
            temperature.toString(),
            iconUrl,
            data.main.feelsLike.toString(),
            data.wind.speed.toString(),
            data.weather[0].description
        )

        Injection.providedUseCase(context).insertWeatherLocal(insertData)


        val pendingIntent = PendingIntent.getActivity(
            context,
            UPDATE_REPEAT,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, WEATHER_NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_cloud)
            .setContentTitle(data.name)
            .setContentText("${data.weather[0].description} | $temperature C")
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                WEATHER_NOTIFICATION_CHANNEL_ID,
                WEATHER_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            notificationBuilder.setChannelId(WEATHER_NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = notificationBuilder.build()
        notificationManager.notify(UPDATE_ID,notification)
    }

    fun cancelUpdateWeather(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WeatherUpdate::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            UPDATE_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
    }


    companion object{
        const val UPDATE_REPEAT = 201
        const val UPDATE_ID = 202
        const val WEATHER_NOTIFICATION_CHANNEL_ID = "update_weather"
        const val WEATHER_NOTIFICATION_CHANNEL_NAME = "update_notification"
    }
}