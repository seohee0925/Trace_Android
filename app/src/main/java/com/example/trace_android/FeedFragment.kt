package com.example.trace_android

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.os.Handler
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
import com.google.android.gms.maps.CameraUpdateFactory
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
    private var userLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        // 레이아웃 인플레이트
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 맵 프래그먼트 설정
        val mapFragment = SupportMapFragment.newInstance().apply {
            getMapAsync(this@FeedFragment)
        }
        childFragmentManager.beginTransaction().replace(R.id.map_container, mapFragment).commit()

        // '내 위치로 돌아오기' 버튼 리스너
        view.findViewById<FloatingActionButton>(R.id.fab_my_location)?.setOnClickListener {
            checkLocationPermissionAndGetLastLocation()
        }

        // '트레이스 추가' 버튼 리스너 설정
        val fabAddTrace = view.findViewById<ExtendedFloatingActionButton>(R.id.fab_add_trace)
        fabAddTrace.setOnClickListener {
            checkLocationPermissionAndGetLastLocation()
            // InputBottomSheetFragment 인스턴스를 생성하고 사용자 위치를 설정합니다.
            val inputBottomSheetFragment = InputBottomSheetFragment().apply {
                userLocation?.let { it1 -> setLocation(it1) }
            }
            inputBottomSheetFragment.show(parentFragmentManager, inputBottomSheetFragment.tag)
        }
    }

    // GoogleMap이 준비되면 호출
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapReady = true
        setMapStyle()
        enableMyLocation()

        mMap.setOnMarkerClickListener { marker ->
            // 지도 뷰를 마커 위치로 이동
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(marker.position, mMap.cameraPosition.zoom)
            // 애니메이션 시간을 줄이기 (예: 150ms)
            mMap.animateCamera(cameraUpdate, 200, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    // 커스텀 다이얼로그 생성 및 표시
                    val dialogFragment = PostDetailsDialogFragment()
                    dialogFragment.show(parentFragmentManager, dialogFragment.tag)
                }

                override fun onCancel() {
                    // 이동 취소 시 할 작업 (여기서는 아무것도 하지 않음)
                }
            })
            true
        }

    }

    // 현재 위치 활성화 및 마커 업데이트
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = false
            checkLocationPermissionAndGetLastLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    // 권한 체크 및 마지막 위치 가져오기
    private fun checkLocationPermissionAndGetLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(
                        com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                            userLocation!!, 20.0f
                        )
                    )
                    updateMarkersBasedOnProximity() // 마커 업데이트
                }
            }
        }
    }

    // 마커 업데이트 기능
    private fun updateMarkersBasedOnProximity() {
        val currentViewport = getCurrentMapViewport()
        CoroutineScope(Dispatchers.IO).launch {
            // 서버에서 포스트 데이터 가져오기
            val response = RetrofitService.apiService.getPostsInArea(
                currentViewport.southwest.latitude, currentViewport.southwest.longitude,
                currentViewport.northeast.latitude, currentViewport.northeast.longitude
            )
            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                withContext(Dispatchers.Main) {
                    mMap.clear()
                    posts.forEach { post ->
                        val postLocation = LatLng(post.latitude, post.longitude)
                        val markerOptions = createMarkerOptions(postLocation, post.content)
                        mMap.addMarker(markerOptions)
                    }
                }
            }
        }
    }

    // 마커 옵션 생성
    private fun createMarkerOptions(postLocation: LatLng, content: String): MarkerOptions {
        val distance = FloatArray(1)
        Location.distanceBetween(userLocation!!.latitude, userLocation!!.longitude, postLocation.latitude, postLocation.longitude, distance)

        val markerIcon = if (distance[0] <= 5) {
            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_activated)
        } else {
            BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_deactivated)
        }

        return MarkerOptions().apply {
            position(postLocation)
            icon(markerIcon)
            if (distance[0] <= 5) title(content) // 20미터 이내 마커에만 제목 설정
        }
    }

    // 현재 카메라 뷰포트 가져오기
    private fun getCurrentMapViewport(): LatLngBounds {
        if (!::mMap.isInitialized) {
            throw IllegalStateException("Map is not initialized yet")
        }
        return mMap.projection.visibleRegion.latLngBounds
    }

    // 맵 스타일 설정
    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            if (!success) Log.e("MapsActivity", "스타일 파싱 실패")
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsActivity", "스타일을 찾을 수 없음", e)
        }
    }

    // 액티비티 재개 시 위치 활성화
    override fun onResume() {
        super.onResume()
        if (mapReady) enableMyLocation()
    }

    // 액티비티 일시정지 시 위치 비활성화
    override fun onPause() {
        super.onPause()
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = false
        }
    }
}
