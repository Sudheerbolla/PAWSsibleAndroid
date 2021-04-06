package com.pawssibleandroid.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.databinding.FragmentMapBinding
import java.io.IOException
import java.util.*

class TrackLocationFragment : Fragment(), View.OnClickListener,
    OnMapReadyCallback {
    private var fragmentMapBinding: FragmentMapBinding? = null
    var mSupportMapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    var ZOOM_LEVEL = 15
    private var mainActivity: MainActivity? = null
    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            mainActivity?.showTopBar("Customer Location", true)
            mainActivity?.hideBottomBar()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()
        if (mSupportMapFragment != null) {
            mSupportMapFragment!!.getMapAsync(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMapBinding =
            DataBindingUtil.inflate(
                inflater,
com.pawssibleandroid.R.layout.fragment_map,
                container,
                false
            )
        initComponents()
        return fragmentMapBinding?.getRoot()
    }

    private fun initComponents() {
        mSupportMapFragment = SupportMapFragment.newInstance()
        val fragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.mapContainer, mSupportMapFragment!!)
        fragmentTransaction.commit()
    }

    override fun onClick(view: View) {}
    var mCurrLocationMarker: Marker? = null
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        if (googleMap != null) {
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            if (ActivityCompat.checkSelfPermission(
                    mainActivity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mainActivity!!,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            googleMap.isMyLocationEnabled = false
            val markerOptions = MarkerOptions()
            val latLng = LatLng(49.2057046, -123.04580960000001)
            markerOptions.position(latLng)
            val locationManager =
                mainActivity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val provider = locationManager.getBestProvider(Criteria(), true)
            val locations = locationManager.getLastKnownLocation(provider!!)
            val providerList = locationManager.allProviders
            if (null != locations && null != providerList && providerList.size > 0) {
                val longitude = locations.longitude
                val latitude = locations.latitude
                val geocoder = Geocoder(
                    mainActivity!!.getApplicationContext(),
                    Locale.getDefault()
                )
                try {
                    val listAddresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (null != listAddresses && listAddresses.size > 0) {
                        val state = listAddresses[0].adminArea
                        val country = listAddresses[0].countryName
                        val subLocality = listAddresses[0].subLocality
                        markerOptions.title("Customer Location: $latLng,$state,$country")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            mCurrLocationMarker = googleMap.addMarker(markerOptions)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL.toFloat())
            googleMap.animateCamera(cameraUpdate)
        }
    }

    companion object {
        fun newInstance(): TrackLocationFragment {
            return TrackLocationFragment()
        }
    }
}