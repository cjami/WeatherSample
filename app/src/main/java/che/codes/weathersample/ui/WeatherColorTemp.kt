package che.codes.weathersample.ui

import android.animation.ArgbEvaluator
import android.content.Context
import androidx.core.content.ContextCompat
import che.codes.weathersample.R


const val HIGH_TEMP_KELVIN = 303.15F
const val LOW_TEMP_KELVIN = 263.15F

class WeatherColorTemp(private val context: Context, private val kelvin: Double) {
    val color: Int
        get() {
            val fraction = (kelvin.toFloat() - LOW_TEMP_KELVIN) / (HIGH_TEMP_KELVIN - LOW_TEMP_KELVIN)
            val cold = ContextCompat.getColor(context, R.color.cold)
            val hot = ContextCompat.getColor(context, R.color.hot)
            return ArgbEvaluator().evaluate(fraction, cold, hot) as Int
        }
}