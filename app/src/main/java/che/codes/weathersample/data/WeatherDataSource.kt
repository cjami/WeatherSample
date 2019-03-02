package che.codes.weathersample.data

import io.reactivex.Observable

interface WeatherDataSource {
    fun fetchWeather(latitude: Double, longitude: Double): Observable<FetchResult>

    enum class FetchStatus {
        SUCCESS,
        AUTH_ERROR,
        GENERAL_ERROR,
        NETWORK_ERROR
    }

    class FetchResult(status: FetchStatus, weather: WeatherInfo?) {
        val status: FetchStatus = status
        val weather: WeatherInfo? = weather
    }
}