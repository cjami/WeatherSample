package che.codes.weathersample.data

import io.reactivex.Observable
import java.util.*

interface WeatherDataSource {
    fun fetchWeather(latitude: Double, longitude: Double): Observable<FetchResult>

    enum class FetchStatus {
        SUCCESS
    }

    class FetchResult(status: FetchStatus, weather: WeatherInfo?) {
        val status: FetchStatus = status
        val weather: WeatherInfo? = weather
    }
}