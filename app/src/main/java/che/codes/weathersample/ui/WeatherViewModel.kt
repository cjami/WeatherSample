package che.codes.weathersample.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import che.codes.weathersample.data.WeatherInfo
import che.codes.weathersample.data.WeatherProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class WeatherViewModel @Inject constructor(private val weatherProvider: WeatherProvider) : ViewModel() {
    val result = MutableLiveData<Result>()
    private val disposables = CompositeDisposable()

    fun fetchWeather() {
        disposables.add(weatherProvider.fetchWeather()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                result.value = Result(Status.FETCHING, null)
            }
            .subscribe { fetchResult ->
                if (fetchResult.weather != null) {
                    result.value = Result(Status.SUCCESS, fetchResult.weather)
                } else {
                    result.value = Result(Status.FAILURE, null)
                }
            })
    }

    override fun onCleared() {
        disposables.clear()
    }

    enum class Status {
        SUCCESS,
        FAILURE,
        FETCHING
    }

    class Result(var status: Status, var weather: WeatherInfo?)
}