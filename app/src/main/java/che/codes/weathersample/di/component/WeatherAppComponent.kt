package che.codes.weathersample.di.component

import che.codes.weathersample.di.module.ContextModule
import che.codes.weathersample.di.module.DataSourceModule
import che.codes.weathersample.di.module.NetworkModule
import che.codes.weathersample.di.module.ViewModelFactoryModule
import che.codes.weathersample.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ContextModule::class, ViewModelFactoryModule::class, NetworkModule::class, DataSourceModule::class])
interface WeatherAppComponent {
    fun inject(activity: MainActivity)
}