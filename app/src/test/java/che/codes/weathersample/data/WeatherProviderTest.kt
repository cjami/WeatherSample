package che.codes.weathersample.data

import che.codes.weathersample.data.WeatherProvider.FetchResult
import che.codes.weathersample.data.location.LocationProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

private const val LAT: Double = 0.0
private const val LON: Double = 0.0
private const val WEATHER_NAME: String = "Cloudy"
private const val WEATHER_TEMP: Double = 18.0

@RunWith(MockitoJUnitRunner::class)
class WeatherProviderTest {

    private lateinit var sut: WeatherProvider
    private val dataSourceMock: WeatherDataSource = mock()
    private val locationProviderMock: LocationProvider = mock()
    private val sampleWeatherInfo: WeatherInfo = WeatherInfo(WEATHER_NAME, WEATHER_TEMP)

    @Before
    fun setUp() {
        sut = WeatherProvider(dataSourceMock, locationProviderMock)

        whenever(locationProviderMock.fetchLocation()).thenReturn(Observable.just(doubleArrayOf(LAT, LON)))
    }

    @Test
    fun getWeather_onSuccess_correctWeatherReturned() {
        success()

        val observable: Observable<FetchResult> = sut.fetchWeather()
        val testObserver: TestObserver<FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        val result: FetchResult = testObserver.values()[0]

        assertNotNull(result.weather)
        assertEquals(result.weather?.name, sampleWeatherInfo.name)
        assertEquals(result.weather?.temperature, sampleWeatherInfo.temperature)
    }

    @Test
    fun getWeather_onAuthError_failureReturned() {
        authError()

        val observable: Observable<FetchResult> = sut.fetchWeather()
        val testObserver: TestObserver<FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        val result: FetchResult = testObserver.values()[0]

        assertEquals(result.status, WeatherProvider.FetchStatus.FAILURE)
    }

    @Test
    fun getWeather_onAuthError_noWeatherReturned() {
        authError()

        val observable: Observable<FetchResult> = sut.fetchWeather()
        val testObserver: TestObserver<FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        val result: FetchResult = testObserver.values()[0]

        assertNull(result.weather)
    }

    @Test
    fun getWeather_onGeneralError_failureReturned() {
        generalError()

        val observable: Observable<FetchResult> = sut.fetchWeather()
        val testObserver: TestObserver<FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        val result: FetchResult = testObserver.values()[0]

        assertEquals(result.status, WeatherProvider.FetchStatus.FAILURE)
    }

    @Test
    fun getWeather_onGeneralError_noWeatherReturned() {
        generalError()

        val observable: Observable<FetchResult> = sut.fetchWeather()
        val testObserver: TestObserver<FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        val result: FetchResult = testObserver.values()[0]

        assertNull(result.weather)
    }

    @Test
    fun getWeather_onNetworkError_failureReturned() {
        networkError()

        val observable: Observable<FetchResult> = sut.fetchWeather()
        val testObserver: TestObserver<FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        val result: FetchResult = testObserver.values()[0]

        assertEquals(result.status, WeatherProvider.FetchStatus.FAILURE)
    }

    @Test
    fun getWeather_onNetworkError_noWeatherReturned() {
        networkError()

        val observable: Observable<FetchResult> = sut.fetchWeather()
        val testObserver: TestObserver<FetchResult> = observable.test()
        testObserver.awaitTerminalEvent()
        val result: FetchResult = testObserver.values()[0]

        assertNull(result.weather)
    }

    //region Helper Methods

    private fun success() {
        whenever(dataSourceMock.fetchWeather(any(), any())).thenReturn(
            Observable.just(
                WeatherDataSource.FetchResult(
                    WeatherDataSource.FetchStatus.SUCCESS,
                    sampleWeatherInfo
                )
            )
        )
    }

    private fun authError() {
        whenever(dataSourceMock.fetchWeather(any(), any())).thenReturn(
            Observable.just(
                WeatherDataSource.FetchResult(
                    WeatherDataSource.FetchStatus.AUTH_ERROR,
                    null
                )
            )
        )
    }

    private fun generalError() {
        whenever(dataSourceMock.fetchWeather(any(), any())).thenReturn(
            Observable.just(
                WeatherDataSource.FetchResult(
                    WeatherDataSource.FetchStatus.GENERAL_ERROR,
                    null
                )
            )
        )
    }

    private fun networkError() {
        whenever(dataSourceMock.fetchWeather(any(), any())).thenReturn(
            Observable.just(
                WeatherDataSource.FetchResult(
                    WeatherDataSource.FetchStatus.NETWORK_ERROR,
                    null
                )
            )
        )
    }

    //endregion
}