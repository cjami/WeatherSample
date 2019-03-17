package che.codes.weathersample.di.module

import android.content.Context
import che.codes.weathersample.data.WeatherDataSource
import che.codes.weathersample.data.location.GpsLocationProvider
import che.codes.weathersample.data.location.LocationProvider
import che.codes.weathersample.data.openweathermap.OpenWeatherMapService
import che.codes.weathersample.data.openweathermap.OpenWeatherMapSource
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
private const val APP_ID = "abaaca6950c4fbd03eb5a31abdacbf6d"

@Module
open class DataSourceModule {

    @Singleton
    @Provides
    fun provideWeatherDataSource(okHttpClient: OkHttpClient): WeatherDataSource {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return OpenWeatherMapSource(
            retrofit.create(OpenWeatherMapService::class.java),
            APP_ID
        )
    }

    @Singleton
    @Provides
    fun provideLocationProvider(context: Context): LocationProvider {
        return GpsLocationProvider(context)
    }
}