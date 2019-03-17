package che.codes.weathersample.ui

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import che.codes.weathersample.R
import che.codes.weathersample.WeatherApplication
import che.codes.weathersample.data.WeatherDataSource
import che.codes.weathersample.data.location.GpsLocationProvider
import che.codes.weathersample.data.location.LocationProvider
import che.codes.weathersample.data.openweathermap.OpenWeatherMapService
import che.codes.weathersample.data.openweathermap.OpenWeatherMapSource
import che.codes.weathersample.di.component.WeatherAppComponent
import che.codes.weathersample.di.module.ContextModule
import che.codes.weathersample.di.module.NetworkModule
import che.codes.weathersample.di.module.ViewModelFactoryModule
import com.jakewharton.espresso.OkHttp3IdlingResource
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

private const val HIGH_TEMP_KELVIN = 303.15F
private const val LOW_TEMP_KELVIN = 263.15F

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, false, false)

    @get:Rule
    val locationPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)!!

    private lateinit var mockServer: MockWebServer
    private lateinit var networkIdlingResource: IdlingResource // We use this to handle async network calls

    @Inject
    lateinit var client: OkHttpClient

    lateinit var context: Context

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        val app = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as WeatherApplication

        val component = DaggerMainActivityTest_TestWeatherAppComponent.builder()
            .testDataSourceModule(TestDataSourceModule(mockServer.url("").toString(), ""))
            .contextModule(ContextModule(app))
            .build()
        app.component = component
        component.inject(this)

        context = app

        networkIdlingResource = OkHttp3IdlingResource.create("OkHttp", client)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun appStarts_showsFetchingText() {
        activityRule.launchActivity(null)
        Espresso.onView((withId(R.id.main_text))).check(matches(withText(R.string.fetching)))
    }

    @Test
    fun appStarts_showsDefaultColor() {
        activityRule.launchActivity(null)
        val defaultColor = context.getColor(R.color.def)
        Espresso.onView((withId(R.id.background))).check(matches(withColor(defaultColor)))
    }

    @Test
    fun afterFetching_onSuccess_showsCorrectText() {
        success()
        registerNetworkIdling()

        activityRule.launchActivity(null)

        Espresso.onView((withId(R.id.main_text))).check((matches(withText("CLOUDS"))))

        unregisterNetworkIdling()
    }

    @Test
    fun afterFetching_onSuccess_showsCorrectColor() {
        success()
        registerNetworkIdling()

        activityRule.launchActivity(null)

        val hotColor = context.getColor(R.color.hot)
        val coldColor = context.getColor(R.color.cold)
        val fraction = (289.5F - LOW_TEMP_KELVIN) / (HIGH_TEMP_KELVIN - LOW_TEMP_KELVIN)
        val correctColor = ArgbEvaluator().evaluate(fraction, coldColor, hotColor) as Int

        Espresso.onView((withId(R.id.background))).check(matches(withColor(correctColor)))

        unregisterNetworkIdling()
    }

    @Test
    fun afterFetching_onAuthError_showsFailureText() {
        authError()
        registerNetworkIdling()

        activityRule.launchActivity(null)

        Espresso.onView((withId(R.id.main_text))).check((matches(withText(R.string.failure))))

        unregisterNetworkIdling()
    }

    @Test
    fun afterFetching_onAuthError_showsDefaultColor() {
        authError()
        registerNetworkIdling()

        activityRule.launchActivity(null)

        val defaultColor = context.getColor(R.color.def)
        Espresso.onView((withId(R.id.background))).check(matches(withColor(defaultColor)))

        unregisterNetworkIdling()
    }

    @Test
    fun afterFetching_onServerError_showsFailureText() {
        serverError()
        registerNetworkIdling()

        activityRule.launchActivity(null)

        Espresso.onView((withId(R.id.main_text))).check((matches(withText(R.string.failure))))

        unregisterNetworkIdling()
    }

    @Test
    fun afterFetching_onServerError_showsDefaultColor() {
        serverError()
        registerNetworkIdling()

        activityRule.launchActivity(null)

        val defaultColor = context.getColor(R.color.def)
        Espresso.onView((withId(R.id.background))).check(matches(withColor(defaultColor)))

        unregisterNetworkIdling()
    }

    //region Helper Classes and Methods

    private fun success() {
        mockServer.enqueue(createJsonResponse(200, getJsonFromFile("owm_response_success.json")))
    }

    private fun authError() {
        mockServer.enqueue(createJsonResponse(401))
    }

    private fun serverError() {
        mockServer.enqueue(createJsonResponse(500))
    }

    private fun createJsonResponse(code: Int, body: String = "{}"): MockResponse {
        return MockResponse().addHeader("Content-Type", "application/json; charset=utf-8")
            .addHeader("Cache-Control", "no-cache")
            .setBody(body)
            .setResponseCode(code)
    }

    private fun getJsonFromFile(filename: String): String {
        val inputStream = InstrumentationRegistry.getInstrumentation().context.assets.open(filename)
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun registerNetworkIdling() {
        IdlingRegistry.getInstance().register(networkIdlingResource)
    }

    private fun unregisterNetworkIdling() {
        IdlingRegistry.getInstance().unregister(networkIdlingResource)
    }

    private fun withColor(color: Int): Matcher<View> {
        return ColorMatcher(color)
    }

    class ColorMatcher(private val expectedColor: Int) : BaseMatcher<View>() {
        private var colorFromView: Int = -1

        override fun matches(item: Any?): Boolean {
            colorFromView = ((item as View).background as ColorDrawable).color
            return colorFromView == expectedColor
        }

        override fun describeTo(description: Description?) {
            description?.appendText("Color $colorFromView")
        }

        override fun describeMismatch(item: Any?, mismatchDescription: Description?) {
            mismatchDescription?.appendText("Color did not match $expectedColor was $colorFromView")
        }
    }

    @Singleton
    @Component(modules = [ContextModule::class, ViewModelFactoryModule::class, NetworkModule::class, TestDataSourceModule::class])
    interface TestWeatherAppComponent : WeatherAppComponent {
        fun inject(test: MainActivityTest)
    }

    @Module
    class TestDataSourceModule(private val baseUrl: String, private val appId: String) {
        @Singleton
        @Provides
        fun provideWeatherDataSource(okHttpClient: OkHttpClient): WeatherDataSource {
            val retrofit = Retrofit.Builder().baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return OpenWeatherMapSource(
                retrofit.create(OpenWeatherMapService::class.java),
                appId
            )
        }

        @Singleton
        @Provides
        fun provideLocationProvider(context: Context): LocationProvider {
            return GpsLocationProvider(context)
        }
    }

    //endregion
}