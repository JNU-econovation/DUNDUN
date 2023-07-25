package com.project981.dundun.view.map

import android.Manifest
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project981.dundun.R
import com.project981.dundun.databinding.FragmentMapBinding
import com.project981.dundun.view.MainViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapPoint.mapPointWithGeoCoord
import net.daum.mf.map.api.MapPoint.mapPointWithScreenLocation
import net.daum.mf.map.api.MapView
import net.daum.mf.map.api.MapView.CurrentLocationEventListener
import net.daum.mf.map.api.MapView.MapViewEventListener
import net.daum.mf.map.api.MapView.POIItemEventListener
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapFragment : Fragment(), MapViewEventListener, CurrentLocationEventListener, POIItemEventListener {
    private val ACCESS_FINE_LOCATION = 1000
    private var mapView: MapView? = null
    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding
        get() = requireNotNull(_binding)
    private val viewModel: MapViewModel by viewModels()
    private val mainViewModel : MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //do something
        mapView = MapView(activity)
        binding.mapView.addView(mapView)
        mapView?.setMapViewEventListener(this)
        mapView?.setCurrentLocationEventListener(this)
        mapView?.setPOIItemEventListener(this)
        binding.recyclerMapList.apply {
            adapter = BottomRecyclerAdapter{ noticeId, artistId ->
                mainViewModel.focusItem = noticeId
                mainViewModel.focusArtist = artistId
                findNavController().navigate(R.id.action_mapFragment_to_myPageFragment)
            }
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.localButton.setOnClickListener {
            if (checkLocationService()) {
                permissionCheck()
            } else {
                Toast.makeText(context, "GPS를 켜주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.viewMapBackground.setOnClickListener {
            animateSheet(7f, 0f)
        }

        binding.btnMapRefresh.setOnClickListener {
            viewModel.getMarkerInfo(
                getPixelGeo(),
                getDistance(),
                mapView!!.mapCenterPoint.mapPointGeoCoord.latitude,
                mapView!!.mapCenterPoint.mapPointGeoCoord.longitude
            ) {
                binding.btnMapRefresh.visibility = View.GONE
            }
        }

        viewModel.markerList.observe(viewLifecycleOwner) {
            mapView?.removeAllPOIItems()
            for (i in it.indices) {
                val temp = MapPOIItem().apply {
                    itemName = "123"
                    isShowCalloutBalloonOnTouch = false
                    tag = i
                    mapPoint =
                        mapPointWithGeoCoord(it[i].lat / it[i].count, it[i].lng / it[i].count)
                    markerType = MapPOIItem.MarkerType.CustomImage
                    val index = if(it[i].count >= 10){
                        9
                    }else{
                        it[i].count-1;
                    }
                    customImageResourceId = MARKER_IMAGE_LIST[index]
                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                    customSelectedImageResourceId = MARKER_IMAGE_LIST[index]
                    isCustomImageAutoscale = true
                    setCustomImageAnchor(0.5f, 1.0f)
                }
                mapView?.addPOIItem(temp)
            }
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if(binding.sheetMapLayout.visibility == View.VISIBLE){
                animateSheet(7f, 0f)
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView = null
        stopTracking()
    }

    private fun getDistance(): Double {
        val r = 6371
        val one = mapPointWithScreenLocation(
            0.0,
            resources.displayMetrics.heightPixels.toDouble()
        ).mapPointGeoCoord
        val two = mapPointWithScreenLocation(
            0.0,
            0.0
        ).mapPointGeoCoord
        val dLat = abs(one.latitude - two.latitude) * (PI / 180)
        val dLng = abs(one.longitude - two.longitude) * (PI / 180)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(one.latitude * (PI / 180)) * cos(two.latitude * (PI / 180)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c * 1000
    }

    private fun permissionCheck() {
        val preference = activity?.getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference?.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // 권한이 없는 상태
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // 권한 거절
                val builder = AlertDialog.Builder(context)
                builder.setMessage(getString(R.string.map_permission_request_test))
                builder.setPositiveButton("확인") { _, _ ->
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION
                    )
                }
                builder.setNegativeButton("취소") { _, _ ->

                }
                builder.show()
            } else {
                if (isFirstCheck == true) {
                    // 최초 권한 요청
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESS_FINE_LOCATION)
                }
                else {
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("현재 위치를 확인하시려면 설정에서 위치 권한을 허용해주세요.")
                    builder.setPositiveButton("설정으로 이동") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("pakage:"+"com.project981.dundun"))
                        startActivity(intent)
                    }
                    builder.setNegativeButton("취소") { _, _ ->

                    }
                    builder.show()
                }
            }
        }
        // 권한 있는 상태
        else {
            startTracking()
        }
    }

    private fun animateSheet(start: Float, end: Float) {
        ValueAnimator.ofFloat(start, end).apply {
            addUpdateListener {
                binding.recyclerMapList.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    it.animatedValue as Float
                )
                binding.viewMapBackground.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    10f - (it.animatedValue as Float)
                )
            }

            doOnEnd {
                if (start > 5f) {
                    binding.sheetMapLayout.visibility = View.GONE
                }
            }
            duration = 300
            repeatCount = 0
            interpolator = AccelerateDecelerateInterpolator()
        }.start()
    }

    // GPS 확인
    private fun checkLocationService(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun startTracking() {
        mapView?.currentLocationTrackingMode =
            MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

    }

    private fun stopTracking() {
        mapView?.currentLocationTrackingMode = MapView.CurrentLocationTrackingMode.TrackingModeOff

    }

    companion object {
        const val ACCESS_FINE_LOCATION = 1000

        val MARKER_IMAGE_LIST = listOf(
            R.drawable.marker_img_1,
            R.drawable.marker_img_2,
            R.drawable.marker_img_3,
            R.drawable.marker_img_4,
            R.drawable.marker_img_5,
            R.drawable.marker_img_6,
            R.drawable.marker_img_7,
            R.drawable.marker_img_8,
            R.drawable.marker_img_9,
            R.drawable.marker_img_10,
        )
    }

    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {
        binding.btnMapRefresh.visibility = View.VISIBLE
        if (p0?.currentLocationTrackingMode == MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading) {
            stopTracking()

        }
    }

    private fun getPixelGeo(): Double {
        val density: Double = resources.displayMetrics.density * (55).toDouble()
        val temp1 = mapPointWithScreenLocation(
            0.0,
            0.0
        ).mapPointGeoCoord.latitude

        val temp2 = mapPointWithScreenLocation(
            0.0,
            density
        ).mapPointGeoCoord.latitude
        return abs(temp1 - temp2)
    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {
        viewModel.getClusterInfo(getPixelGeo())
    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {

    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {

    }

    override fun onCurrentLocationUpdateFailed(p0: MapView?) {

    }

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {

    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        binding.recyclerMapList.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0f)
        binding.viewMapBackground.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10f)
        binding.sheetMapLayout.visibility = View.VISIBLE
        animateSheet(0f, 7f)
        if (viewModel.markerList.value != null) {
            (binding.recyclerMapList.adapter as BottomRecyclerAdapter).setDate(
                viewModel.markerList.value!![p1!!.tag].noticeList
            )
        }
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }
}