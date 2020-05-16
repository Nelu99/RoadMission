package com.example.roadmission

import android.os.AsyncTask
import com.androdocs.httprequest.HttpRequest
import org.json.JSONObject

class Weather:AsyncTask<String, Void, String>() {
    val API:String = "7779405fad99f4ae488c0aed79e13d92"

    var latitude:String = ""
    var longitude:String = ""
    var temp:String = "dsadsa"
    var press:String = ""
    var humi:String = ""
    var sunr:Long = 0
    var suns:Long = 0
    var windSpeed:String = ""
    var weatherDesc:String = ""

    override fun doInBackground(vararg args: String): String {
        latitude = MainActivity.myService.getLatitude()
        longitude = MainActivity.myService.getLongitude()
        var response:String = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude  + "&units=metric&appid=" + API)
        return response
    }

    override fun onPostExecute(result:String) {
            var jsonObj:JSONObject = JSONObject(result)
            var main:JSONObject = jsonObj.getJSONObject("main")
            var sys:JSONObject = jsonObj.getJSONObject("sys")
            var wind:JSONObject = jsonObj.getJSONObject("wind")
            var weather:JSONObject = jsonObj.getJSONArray("weather").getJSONObject(0)
            temp = main.getString("temp")+ "°C"
            press = main.getString("pressure")
            humi = main.getString("humidity")
            sunr = sys.getLong("sunrise")
            suns = sys.getLong("sunset")
            windSpeed = wind.getString("speed")
            weatherDesc = weather.getString("description")
    }

    fun getTemperature():String {
        return temp
    }

    fun getPressure():String {
        return press
    }

    fun getHumidity():String {
        return humi
    }

    fun getSunrise():Long {
        return sunr
    }

    fun getSunset():Long {
        return suns
    }

    fun getWind():String
    {
        return windSpeed
    }

    fun getWeatherDescription():String {
        return weatherDesc
    }
}