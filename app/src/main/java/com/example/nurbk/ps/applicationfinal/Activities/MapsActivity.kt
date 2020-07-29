package com.example.nurbk.ps.applicationfinal.Activities

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nurbk.ps.applicationfinal.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.text.DecimalFormat
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private val share
        get() =
            getSharedPreferences("File", Context.MODE_PRIVATE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapG) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.isMyLocationEnabled = true

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style))

        val location = intent.getStringExtra("location")
        if (location != null) {

            val lat = location.substring(location.indexOf("-") + 1).toDouble()
            Log.e("ttt", lat.toString())
            val lng = location.substring(0, location.indexOf("-")).toDouble()
            val latU = share.getString("lat", "")!!.toDouble()
            val lngU = share.getString("lng", "")!!.toDouble()

            val latLng1 = LatLng(lat, lng)
            val markerOptions1 = MarkerOptions()
            markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            mMap.addMarker(
                markerOptions1.position(latLng1).title("Product Location").snippet("Products")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12f))

            val latLng2 = LatLng(latU, lngU)
            val markerOptions2 = MarkerOptions()
            markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            mMap.addMarker(
                markerOptions1.position(latLng2).title("My Location").snippet("My")
            )


            mMap.addPolyline(
                PolylineOptions().add(LatLng(lat, lng))
                    .add(LatLng(latU, lngU))
                    .visible(true)
                    .color(Color.BLUE)
            )
            calculateDistance(latLng1, latLng2)
        } else {
            val lat = share.getString("lat", "")!!.toDouble()
            val lng = share.getString("lng", "")!!.toDouble()
            val latLng1 = LatLng(lat, lng)
            val markerOptions1 = MarkerOptions()
            markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            mMap.addMarker(
                markerOptions1.position(latLng1).title("My Location").snippet("")
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12f))

        }


    }


    private fun calculateDistance(x: LatLng, y: LatLng) {
        val radius = 6371
        val lat1 = x.latitude
        val lon1 = x.longitude
        val lat2 = y.latitude
        val lon2 = y.longitude

        val lat = Math.toRadians(lat2 - lat1)
        val lon = Math.toRadians(lon2 - lon1)
        val a = sin(lat / 2) * sin(lat / 2) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(lon / 2) * sin(lon / 2)

        val c = 2 * asin(sqrt(a))
        val valueResult = radius * c
        val km = valueResult / 1
        val newFormat = DecimalFormat("####")
        val kmInDec = Integer.valueOf(newFormat.format(km))
        val meter = valueResult % 1000
        val meterInDec = Integer.valueOf(newFormat.format(meter))
        Log.e("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter " + meterInDec)
    }


}
