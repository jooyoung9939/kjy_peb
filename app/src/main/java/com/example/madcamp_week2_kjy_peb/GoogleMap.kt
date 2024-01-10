package com.example.madcamp_week2_kjy_peb

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.madcamp_week2_kjy_peb.databinding.ActivityGoogleMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale

internal class GoogleMap : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val TAG = "GoogleMap"
        const val REQUEST_LOCATION_PERMISSION = 1001
    }

    lateinit var binding: ActivityGoogleMapBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var currentMarker: Marker? = null
    private var isMapReady = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkLocationPermission()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLastLocation()
        } else {
            // 권한이 없으면 권한을 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            if (checkLocationPermission()) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
            } else {

            }
        }

        this.mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this@GoogleMap)

        binding.ok.setOnClickListener{
            val markerLocation = getMarkerLocation()
            var address: String? = null
            if(markerLocation != null){
                address = getAddressFromLocation(markerLocation)
            }
            if(intent.getStringExtra("prev").equals("Register")){
                val resultIntent = Intent()
                resultIntent.putExtra("address", address)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            else {
                val resultIntent = Intent()
                val token = intent.getStringExtra("token") ?: ""
                resultIntent.putExtra("token", token)
                resultIntent.putExtra("address", address)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
            } else {
                // 권한이 거부되었을 때 처리
                // 필요한 로직을 추가하세요
            }
        }
    }

    private fun getMarkerLocation(): LatLng? {
        return currentMarker?.position?.let { position ->
            LatLng(position.latitude, position.longitude)
        }
    }

    private fun getLastLocation(){
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null && isMapReady) {
                        // 위치를 얻어와서 마커를 설정
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        currentMarker = setupMarker(LatLngEntity(location.latitude, location.longitude))
                        currentMarker?.showInfoWindow()
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    }
                }
        } else {
            // 권한이 없으면 권한을 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }
    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        isMapReady = true

        if (checkLocationPermission()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            getLastLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            // 권한이 없으면 기본 위치로 마커 설정
        }
    }
    fun getAddressFromLocation(latLng: LatLng): String? {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val locality = address.adminArea // 시
                val subLocality = address.subLocality // 구

                // 시와 구를 합쳐서 주소로 반환
                return "$locality $subLocality"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }


    /**
     * setupMarker()
     * 선택한 위치의 marker 표시
     * @param locationLatLngEntity
     * @return
     */
    private fun setupMarker(locationLatLngEntity: LatLngEntity): Marker? {

        val positionLatLng = LatLng(locationLatLngEntity.latitude!!,locationLatLngEntity.longitude!!)
        val markerOption = MarkerOptions().apply {
            position(positionLatLng)
            title("현재 위치")
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID  // 지도 유형 설정
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 15f))  // 카메라 이동
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))  // 줌의 정도 - 1 일 경우 세계지도 수준, 숫자가 커질 수록 상세지도가 표시됨
        return googleMap.addMarker(markerOption)

    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }


    /**
     * LatLngEntity data class
     *
     * @property latitude 위도 (ex. 37.5562)
     * @property longitude 경도 (ex. 126.9724)
     */
    data class LatLngEntity(
        var latitude: Double?,
        var longitude: Double?
    )
}