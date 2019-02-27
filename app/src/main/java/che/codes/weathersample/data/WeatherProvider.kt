package che.codes.weathersample.data

import io.reactivex.Observable

class WeatherProvider {
    var dataSource: WeatherDataSource
    var locationProvider: LocationProvider

    constructor(dataSource: WeatherDataSource, locationProvider: LocationProvider) {
        this.dataSource = dataSource
        this.locationProvider = locationProvider
    }

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
        }
    }

    enum class FetchStatus {
        SUCCESS
    }

    class FetchResult(status: FetchStatus, weather: WeatherInfo?) {
        val status: FetchStatus = status
        val weather: WeatherInfo? = weather
    }
}