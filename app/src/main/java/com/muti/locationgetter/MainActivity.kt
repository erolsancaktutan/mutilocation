package com.muti.locationgetter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.muti.location.MutiLocation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var mutiLocation:MutiLocation?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mutiLocation = MutiLocation(this)
        mutiLocation!!.setAccuracy(1,0.5f)

        mutiLocation!!.getCurrentLocation().observe(this, Observer { t ->
            tv.text = tv.text.toString()+ "Lat: "+t.latitude.toString()+ "  Long: "+t.longitude.toString()+"\n"
            //mutiLocation!!.stop()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            1461->{
                if (mutiLocation!!.isReadyLocationService()){
                    getLocation()
                }
            }
        }
    }
}
