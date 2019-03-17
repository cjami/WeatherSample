package che.codes.weathersample.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import che.codes.weathersample.R
import che.codes.weathersample.WeatherApplication
import che.codes.weathersample.ui.WeatherViewModel.Result
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: WeatherViewModelFactory

    lateinit var background: View
    lateinit var mainText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.main)

        (application as WeatherApplication).component.inject(this)

        background = findViewById(R.id.background)
        mainText = findViewById(R.id.main_text)

        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(WeatherViewModel::class.java)

        viewModel.result.observe(this, Observer<Result> { result -> processResult(result) })
    }

    private fun processResult(result: Result?) {
        if (result == null) {
            return
        }

        when (result.status) {
            WeatherViewModel.Status.FETCHING -> {
                mainText.setText(R.string.fetching)
                background.setBackgroundColor(ContextCompat.getColor(this, R.color.def))
            }

            WeatherViewModel.Status.SUCCESS -> {
                mainText.text = result.weather?.name?.toUpperCase()
                background.setBackgroundColor(WeatherColorTemp(this, result.weather?.temperature!!).color)
            }

            WeatherViewModel.Status.FAILURE -> {
                mainText.setText(R.string.failure)
                background.setBackgroundColor(ContextCompat.getColor(this, R.color.def))
            }
        }
    }
}