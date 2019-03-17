package che.codes.weathersample.di.module

import che.codes.weathersample.data.WeatherProvider
import che.codes.weathersample.ui.WeatherViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelFactoryModule {

    @Provides
    fun provideViewModelFactory(weatherProvider: WeatherProvider): WeatherViewModelFactory {
        return WeatherViewModelFactory(weatherProvider)
    }
}