package che.codes.weathersample

import android.app.Application
import che.codes.weathersample.di.component.DaggerWeatherAppComponent
import che.codes.weathersample.di.component.WeatherAppComponent
import che.codes.weathersample.di.module.ContextModule

class WeatherApplication : Application() {
    lateinit var component: WeatherAppComponent

    override fun onCreate() {
        super.onCreate()
        component = DaggerWeatherAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()
    }
}