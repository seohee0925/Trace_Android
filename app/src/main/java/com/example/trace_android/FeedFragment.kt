package com.example.trace_android

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions

class FeedFragment : Fragment(), OnMapReadyCallback {


    private val REQUEST_ACCESS_FINE_LOCATION = 1000
    private lateinit var mMap: GoogleMap
    private var mapReady = false
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // onCreateView에서 레이아웃을 인플레이트하고 초기화를 진행합니다.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    // onViewCreated에서 SupportMapFragment를 초기화하고, 지도가 준비되면 콜백을 설정합니다.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.map_container, mapFragment).commit()
        mapFragment.getMapAsync(this)

    }

    // onMapReady에서 GoogleMap 객체를 초기화하고 사용자의 위치를 업데이트합니다.
    // res/raw/map_style.json의 맵 스타일은 import합니다.
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapReady = true

        if (context != null) {
            try {
                val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
                val success = mMap.setMapStyle(style)
                if (!success) {
                    Log.e("MapsActivity", "스타일 파싱 실패")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e("MapsActivity", "스타일을 찾을 수 없음", e)
            }
        } else {
            Log.e("MapsActivity", "Context가 null입니다.")
        }
        updateLocationUI()
    }


    // updateLocationUI는 사용자의 현재 위치를 지도에 표시합니다.
    private fun updateLocationUI() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        if (mapReady) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val userLocation = LatLng(it.latitude, it.longitude)
                        mMap.moveCamera(
                            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                userLocation,
                                20.0f
                            )
                        )
                        // Removed the marker addition line
                    }
                }
        }

    }
}