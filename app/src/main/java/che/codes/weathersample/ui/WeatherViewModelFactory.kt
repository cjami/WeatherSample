package che.codes.weathersample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import che.codes.weathersample.data.WeatherProvider
import javax.inject.Inject

class WeatherViewModelFactory @Inject constructor(private val weatherProvider: WeatherProvider) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WeatherViewModel::class.java) -> {
                WeatherViewModel(weatherProvider) as T
            }
            else -> throw IllegalArgumentException(
                "${modelClass.simpleName} is an unknown view model type"
            )
        }
    }
}