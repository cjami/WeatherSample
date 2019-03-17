package che.codes.weathersample.data.openweathermap

import che.codes.weathersample.data.WeatherDataSource
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

private const val APP_ID: String = "appId"
private const val LAT: Double = 0.0
private const val LON: Double = 0.0
private const val WEATHER_NAME: String = "Cloudy"
private const val WEATHER_TEMP: Double = 18.0

@RunWith(MockitoJUnitRunner::class)
class OpenWeatherMapSourceTest {
    private lateinit var sut: OpenWeatherMapSource
    private val openWeatherMapServiceMock: OpenWeatherMapService = mock()

    @Before
    fun setUp() {
        sut = OpenWeatherMapSource(openWeatherMapServiceMock, APP_ID)
    }

    @Test
    fun fetchWeather_givenAppId_appIdPassedToService() {
        sut.fetchWeather(LAT, LON)

        val observable: Observable<WeatherDataSource.FetchResult> = sut.fetchWeather(LAT, LON)
        val testObserver: TestObserver<WeatherDataSource.FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()

        verify(openWeatherMapServiceMock).getWeatherData(any(), any(), eq(APP_ID))
    }

    @Test
    fun fetchWeather_onSuccess_returnSuccess() {
        success()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertEquals(result.status, WeatherDataSource.FetchStatus.SUCCESS)
    }

    @Test
    fun fetchWeather_onSuccess_returnCorrectWeatherInfo() {
        success()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertEquals(result.weather?.name, WEATHER_NAME)
        assertEquals(result.weather?.temperature, WEATHER_TEMP)
    }

    @Test
    fun fetchWeather_onAuthError_returnAuthError() {
        authError()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertEquals(result.status, WeatherDataSource.FetchStatus.AUTH_ERROR)
    }

    @Test
    fun fetchWeather_onAuthError_nullWeatherInfoReturned() {
        authError()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertNull(result.weather)
    }

    @Test
    fun fetchWeather_onGeneralError_returnGeneralError() {
        generalError()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertEquals(result.status, WeatherDataSource.FetchStatus.GENERAL_ERROR)
    }

    @Test
    fun fetchWeather_onGeneralError_nullWeatherInfoReturned() {
        generalError()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertNull(result.weather)
    }

    @Test
    fun fetchWeather_onNetworkError_returnNetworkError() {
        networkError()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertEquals(result.status, WeatherDataSource.FetchStatus.NETWORK_ERROR)
    }

    @Test
    fun fetchWeather_onNetworkError_nullWeatherInfoReturned() {
        networkError()

        sut.fetchWeather(LAT, LON)

        val result = getResult(sut.fetchWeather(LAT, LON))

        assertNull(result.weather)
    }

    //region Helper Methods

    private fun getResult(observable: Observable<WeatherDataSource.FetchResult>): WeatherDataSource.FetchResult{
        val testObserver: TestObserver<WeatherDataSource.FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        return testObserver.values()[0]
    }

    private fun success() {
        val weatherData = OpenWeatherMapWeatherData()
        weatherData.weather = arrayOf(OpenWeatherMapWeatherData.Weather())
        weatherData.weather[0].main = WEATHER_NAME
        weatherData.main.temp = WEATHER_TEMP

        whenever(openWeatherMapServiceMock.getWeatherData(any(), any(), any())).thenReturn(
            Observable.just(
                weatherData
            )
        )
    }

    private fun authError() {
        val httpException = HttpException(Response.error<OpenWeatherMapWeatherData>(401, ResponseBody.create(null, "")))
        whenever(openWeatherMapServiceMock.getWeatherData(any(), any(), any())).thenReturn(
            Observable.error(
                httpException
            )
        )
    }

    private fun generalError() {
        val httpException = HttpException(Response.error<OpenWeatherMapWeatherData>(500, ResponseBody.create(null, "")))
        whenever(openWeatherMapServiceMock.getWeatherData(any(), any(), any())).thenReturn(
            Observable.error(
                httpException
            )
        )
    }

    private fun networkError() {
        val ioException = IOException()
        whenever(openWeatherMapServiceMock.getWeatherData(any(), any(), any())).thenReturn(
            Observable.error(
                ioException
            )
        )
    }

    //endregion
}