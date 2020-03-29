package com.muti.locationgetter

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.muti.location.MutiLocation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mutiLocation:MutiLocation?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mutiLocation = MutiLocation(this)

        mutiLocation!!.getCurrentLocation().observe(this, Observer { t ->
            btn.text = "Lat: "+t.latitude.toString()+ "\nLong: "+t.longitude.toString()
        })

        btn.setOnClickListener(View.OnClickListener {
            getLocation()
        })
    }

    private fun getLocation(){
        mutiLocation!!.getLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1453-> {
                if (grantResults.isNotEmpty() &&
                    mutiLocation!!.hasAllPermissionsGranted(grantResults)) {
                   getLocation()
                } else {

                }
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
