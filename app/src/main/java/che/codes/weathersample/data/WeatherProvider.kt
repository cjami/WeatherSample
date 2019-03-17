package che.codes.weathersample.data

import che.codes.weathersample.data.location.LocationProvider
import io.reactivex.Observable
import javax.inject.Inject

class WeatherProvider @Inject constructor(
    private val dataSource: WeatherDataSource,
    private val locationProvider: LocationProvider
) {

    fun fetchWeather(): Observable<FetchResult> {
        // When subscribed to, fetches location, then fetches weather with given location
        return Observable.create { subscriber ->
            locationProvider.fetchLocation().subscribe { location ->
                dataSource.fetchWeather(location[0], location[1]).subscribe { result ->
                    subscriber.onNext(FetchResult(convertStatus(result.status), result.weather))
                    subscriber.onComplete()
                }
            }
        }
    }

    private fun convertStatus(sourceStatus: WeatherDataSource.FetchStatus): FetchStatus {
        when (sourceStatus) {
            WeatherDataSource.FetchStatus.SUCCESS -> return FetchStatus.SUCCESS
            else -> return FetchStatus.FAILURE
        }
    }

    enum class FetchStatus {
        SUCCESS,
        FAILURE
    }

    class FetchResult(val status: FetchStatus, val weather: WeatherInfo?)
}