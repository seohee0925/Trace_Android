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
import android.widget.Toast
import com.example.trace_android.retrofit.RetrofitService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // '내 위치로 돌아오기' 버튼 리스너 설정
        view.findViewById<FloatingActionButton>(R.id.fab_my_location)?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            val userLocation = LatLng(it.latitude, it.longitude)
                            mMap.animateCamera(
                                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                                    userLocation,
                                    17.0f
                                )
                            )
                        }
                    }
            }
        }

        // FAB 버튼 참조를 찾고 클릭 리스너를 설정합니다.
        val fabAddTrace = view.findViewById<ExtendedFloatingActionButton>(R.id.fab_add_trace)
        fabAddTrace.setOnClickListener {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    // InputBottomSheetFragment 인스턴스를 생성하고 사용자 위치를 설정합니다.
                    val inputBottomSheetFragment = InputBottomSheetFragment().apply {
                        setLocation(userLocation)
                    }
                    inputBottomSheetFragment.show(parentFragmentManager, inputBottomSheetFragment.tag)
                }
            }
        }

    }

    // onMapReady에서 GoogleMap 객체를 초기화하고 사용자의 위치를 업데이트합니다.
    // res/raw/map_style.json의 맵 스타일은 import합니다.
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapReady = true

        mMap.setOnCameraIdleListener {
            val currentViewport = getCurrentMapViewport()
            val southWest = currentViewport.southwest
            val northEast = currentViewport.northeast

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitService.apiService.getPostsInArea(
                        southWest.latitude, southWest.longitude,
                        northEast.latitude, northEast.longitude
                    )
                    if (response.isSuccessful) {
                        val posts = response.body() ?: emptyList()
                        withContext(Dispatchers.Main) {
                            // 맵에 포스트 마커 추가
                            posts.forEach { post ->
                                val postLocation = LatLng(post.latitude, post.longitude)
                                val markerOptions = MarkerOptions()
                                    .position(postLocation)
                                    .title(post.content)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_activated)) // 사용자 정의 마커 아이콘 사용
                                mMap.addMarker(markerOptions)
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            // 서버 오류 처리
                            Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // 네트워크 오류 처리
                        Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        setMapStyle()
        enableMyLocation() // 현재 위치 활성화 함수 호출
    }

    private fun setMapStyle() {
        try {
            val style = MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            val success = mMap.setMapStyle(style)
            if (!success) {
                Log.e("MapsActivity", "스타일 파싱 실패")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "스타일을 찾을 수 없음", e)
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(
                        com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                            userLocation,
                            17.0f
                        )
                    )
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    // 현재 화면에 보이는 만큼의 좌표 범위를 리턴.
    // 화면에 보이는 부분만큼만 Pin 정보를 업데이트하기 위해 사용.
    private fun getCurrentMapViewport(): LatLngBounds {
        if (!::mMap.isInitialized) {
            throw IllegalStateException("Map is not initialized yet")
        }

        val projection = mMap.projection
        val visibleRegion = projection.visibleRegion
        return visibleRegion.latLngBounds
    }


    override fun onResume() {
        super.onResume()
        if (mapReady) {
            enableMyLocation()
        }
    }


    override fun onPause() {
        super.onPause()
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = false
        }
    }

}