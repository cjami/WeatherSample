package che.codes.weathersample.data.openweathermap

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("weather")
    fun getWeatherData(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("appid") appId: String) : Observable<OpenWeatherMapWeatherData>
}