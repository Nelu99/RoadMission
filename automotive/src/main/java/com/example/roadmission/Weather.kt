package com.example.roadmission

import android.content.Context
import android.os.AsyncTask
import com.androdocs.httprequest.HttpRequest
import org.json.JSONObject

class Weather:AsyncTask<String, Void, String>() {
    private val API:String = "7779405fad99f4ae488c0aed79e13d92"

    private var latitude:String = ""
    private var longitude:String = ""
    private var weatherDesc:String = ""

    override fun doInBackground(vararg args: String): String {
        latitude = MainActivity.myService.getLatitude()
        longitude = MainActivity.myService.getLongitude()
        return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&units=metric&appid=$API")
    }

    override fun onPostExecute(result:String) {
        val sharedPrefs = MainActivity.myContext.getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        val jsonObj = JSONObject(result)
        val weather:JSONObject = jsonObj.getJSONArray("weather").getJSONObject(0)
        weatherDesc = weather.getString("description")
        with (sharedPrefs.edit()) {
            putString("weather", weatherDesc)
            apply()
        }
    }
}