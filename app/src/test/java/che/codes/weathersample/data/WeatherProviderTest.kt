package che.codes.weathersample.data

import che.codes.weathersample.data.WeatherProvider.FetchResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherProviderTest {

    private lateinit var sut: WeatherProvider
    private val dataSourceMock: WeatherDataSource = mock()
    private val locationProviderMock: LocationProvider = mock()
    private val sampleWeatherInfo: WeatherInfo = WeatherInfo("Cloudy", 15.0)

    @Before
    fun setUp() {
        sut = WeatherProvider(dataSourceMock, locationProviderMock)

        whenever(locationProviderMock.fetchLocation()).thenReturn(Observable.just(doubleArrayOf(0.0, 0.0)))
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

    private fun success(){
        whenever(dataSourceMock.fetchWeather(any(), any())).thenReturn(Observable.just(WeatherDataSource.FetchResult(WeatherDataSource.FetchStatus.SUCCESS, sampleWeatherInfo)))
    }
}