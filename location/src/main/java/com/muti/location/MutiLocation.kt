package com.muti.location

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData

class MutiLocation(private val context: Context): Service(), LocationListener {
    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    private var canGetLocation = false
    private var mLocation: Location?=null
    private var mutableLocation= MutableLiveData<Location>()
    private lateinit var locationManager: LocationManager
    private var MIN_DISTANCE_CHANGE_FOR_UPDATES: Float =
        1f // The minimum distance to change Updates in meters
    private var MIN_TIME_BW_UPDATES: Long =
        10 * 60 * 1 // The minimum time between updates in milliseconds

    companion object {
        private var INSTANCE: MutiLocation? = null
        fun getInstance(activity: Activity): MutiLocation {
            if (INSTANCE == null) {
                INSTANCE = MutiLocation(activity)
            }
            return INSTANCE!!
        }
    }

    fun setAccuracy(second:Int, meter:Float){
        MIN_TIME_BW_UPDATES= (1000*second*1).toLong()
        MIN_DISTANCE_CHANGE_FOR_UPDATES= meter
    }

    fun isReadyLocationService(): Boolean {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        canGetLocation = isGPSEnabled || isNetworkEnabled
        return canGetLocation
    }

    fun getLocation() {
        if (isReadyLocationService()) {
            if (isNetworkEnabled || isGPSEnabled) {
                checkOfPermissions()
            }
        } else {
            showSettingsAlert()
        }
    }

    private fun checkOfPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    context as Activity, arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    1453
                )
            }
            else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    )
                    if (locationManager != null) {
                        mLocation =
                            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                    }
                }else if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        mLocation = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            }
        }else{
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                )
                if (locationManager != null) {
                    mLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                }
            }else if (isGPSEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    mLocation = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }
        }
    }

    fun getCurrentLocation():MutableLiveData<Location>{
        return mutableLocation
    }

    private fun showSettingsAlert() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
        alertDialog
            .setMessage(context.getString(R.string.location_alert))

        alertDialog.setPositiveButton(context.getString(R.string.yes),
            DialogInterface.OnClickListener { dialog, which ->
                (context as Activity).startActivityForResult(
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1461, null)
            })
        alertDialog.setNegativeButton(context.getString(R.string.no),
            DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
        alertDialog.show()
    }

    fun hasAllPermissionsGranted(grantResults:IntArray): Boolean {
        for (result in grantResults){
            if (result == PackageManager.PERMISSION_DENIED)
                return false
        }
        return true
    }

    fun stop(){
        locationManager.removeUpdates(this);
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(location: Location?) {
        mutableLocation.value=location
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderEnabled(provider: String?) {}

    override fun onProviderDisabled(provider: String?) {}
}