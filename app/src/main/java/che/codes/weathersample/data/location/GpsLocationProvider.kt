package che.codes.weathersample.data.location

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import io.reactivex.Observable
import javax.inject.Inject

class GpsLocationProvider @Inject constructor(private val context: Context) :
    LocationProvider {

    override fun fetchLocation(): Observable<DoubleArray> {
        return Observable.create { subscriber ->
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        subscriber.onNext(doubleArrayOf(location.latitude, location.longitude))
                        subscriber.onComplete()
                    }

                    // TODO: Handle case where location is null
                }
            } else {
                Log.e("GpsLocationProvider", "Permission ACCESS_COARSE_LOCATION not granted")
            }
        }
    }
}