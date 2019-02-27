package che.codes.weathersample.data

import io.reactivex.Observable

interface LocationProvider {
    fun fetchLocation(): Observable<DoubleArray>
}