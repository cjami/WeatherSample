package che.codes.weathersample.data.openWeatherMap

class OpenWeatherMapWeatherData {
    var weather: Weather = Weather()
    var main: Main = Main()

    class Weather {
        var main: String = ""
    }

    class Main {
        var temp: Double = 0.0
    }
}