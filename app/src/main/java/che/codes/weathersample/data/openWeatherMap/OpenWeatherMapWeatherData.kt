package che.codes.weathersample.data.openweathermap

class OpenWeatherMapWeatherData {
    var weather: Array<Weather> = arrayOf()
    var main: Main = Main()

    class Weather {
        var main: String = ""
    }

    class Main {
        var temp: Double = 0.0
    }
}