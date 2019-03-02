package che.codes.weathersample.data.openWeatherMap

import che.codes.weathersample.data.WeatherDataSource
import che.codes.weathersample.data.WeatherDataSource.*
import che.codes.weathersample.data.WeatherInfo
import io.reactivex.Observable
import retrofit2.HttpException
import java.io.IOException

private const val HTTP_UNAUTHORIZED: Int = 401

class OpenWeatherMapSource(val service: OpenWeatherMapService, val appId: String) : WeatherDataSource {
    override fun fetchWeather(latitude: Double, longitude: Double): Observable<FetchResult> {
        return Observable.create { subscriber ->
            service.getWeatherData(latitude, longitude, appId).subscribe({ result ->
                val weatherInfo = WeatherInfo(result.weather.main, result.main.temp)
                subscriber.onNext(FetchResult(FetchStatus.SUCCESS, weatherInfo))
                subscriber.onComplete()
            },
                { error ->
                    subscriber.onNext(FetchResult(convertError(error), null))
                    subscriber.onComplete()
                })
        }
    }

    private fun convertError(error: Throwable): FetchStatus {
        return when (error) {
            is HttpException -> convertHttpException(error)
            is IOException -> FetchStatus.NETWORK_ERROR
            else -> FetchStatus.GENERAL_ERROR
        }
    }

    private fun convertHttpException(error: HttpException): FetchStatus {
        return when (error.code()) {
            HTTP_UNAUTHORIZED -> FetchStatus.AUTH_ERROR
            else -> FetchStatus.GENERAL_ERROR
        }
    }
}