package che.codes.weathersample.data.location

import io.reactivex.Observable

interface LocationProvider {
    fun fetchLocation(): Observable<DoubleArray>
}