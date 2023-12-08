package com.example.pointofmaps.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.*
import com.example.pointofmaps.clusterlist.TabClusterListDisplayActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.*
import kotlinx.coroutines.launch


class ImgMapFragment : Fragment(), OnMapReadyCallback, ClusterManager.OnClusterClickListener<UserImg>, ClusterManager.OnClusterItemClickListener<UserImg>, ClusterManager.OnClusterItemInfoWindowClickListener<UserImg> {

    private lateinit var mMap: GoogleMap
    private lateinit var mClusterManager: ClusterManager<UserImg>
    private lateinit var mLatLongImgList: ArrayList<UserImg>
    private lateinit var mViewModel: MapClusterViewModel

    private lateinit var mClickedClusterItemInfo: UserImg
    private lateinit var mClickedClusterListInfo: ArrayList<UserImg>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_maps, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.image_map) as SupportMapFragment?

        mViewModel = ViewModelProvider(this)[MapClusterViewModel::class.java]

        mapFragment?.getMapAsync(this)

        return rootView
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(FeatureValues.AppName, "onMapReady")
        mMap = googleMap
        mClusterManager = ClusterManager<UserImg>(context, mMap)

        mClusterManager.setOnClusterClickListener(this)
        mClusterManager.setOnClusterItemClickListener(this)
        mClusterManager.setOnClusterItemInfoWindowClickListener(this)

        mMap.setOnCameraIdleListener(mClusterManager)

        mMap.setOnMarkerClickListener(mClusterManager)
        mMap.setInfoWindowAdapter(mClusterManager.markerManager)
        mMap.setOnInfoWindowClickListener(mClusterManager)
        mClusterManager.markerCollection.setInfoWindowAdapter(ClusterItemInfoWindowAdapter(context))

        mLatLongImgList = ArrayList<UserImg>()
        mClickedClusterItemInfo = UserImg()

        mViewModel.getAllImgClusters().observe(this) { allImgList ->
            if (allImgList.isEmpty()) {
                return@observe
            }

            mLatLongImgList = allImgList as ArrayList<UserImg>
            mLatLongImgList.sortByDescending { userImg -> userImg.imageDateTaken }
            mClusterManager.addItems(mLatLongImgList)
            mClusterManager.cluster()

            val latLng = mLatLongImgList[0].getLatLong()
            Log.i(FeatureValues.AppName, "last image : " + mLatLongImgList[0].imageDataPath)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 15f))
        }

        Log.i(FeatureValues.AppName, "onMapReady : end")
    }

    override fun onClusterClick(cluster: Cluster<UserImg>?): Boolean {
        Log.i(FeatureValues.AppName, "onClusterClick")
        mClickedClusterListInfo = ArrayList<UserImg>()
        for (info in cluster!!.items) {
            mClickedClusterListInfo.add(info)
        }
        val intent = Intent(context, TabClusterListDisplayActivity::class.java)
        intent.putExtra(FeatureValues.IMG_ARRAYLIST, mClickedClusterListInfo)
        startActivity(intent)

        return false
    }

    override fun onClusterItemClick(userImg: UserImg?): Boolean {
        Log.i(FeatureValues.AppName, "onClusterItemClick")
        if (userImg != null) {
            mClickedClusterItemInfo = userImg
        }
        return false
    }

    override fun onClusterItemInfoWindowClick(userImg: UserImg?) {
        Log.i(FeatureValues.AppName, "onClusterItemInfoWindowClick")
        val intent = Intent(context, TabImgDisplayActivity::class.java)

        intent.putExtra(FeatureValues.IMG_ID, userImg!!.imageID)
        intent.putExtra(FeatureValues.IMG_PATH, userImg.imageDataPath)
        intent.putExtra(FeatureValues.IMG_FILENAME, userImg.imageDisplayName)
        intent.putExtra(FeatureValues.IMG_ADDRESS, userImg.imageAddress)
        intent.putExtra(FeatureValues.IMG_ORIENTATION, userImg.imageOri)
        startActivity(intent)
    }

    inner class ClusterItemInfoWindowAdapter(context: Context?) : GoogleMap.InfoWindowAdapter {
        private var mWindow = LayoutInflater.from(requireContext()).inflate(R.layout.info_popup_layout, null)

        override fun getInfoWindow(marker: Marker): View? {
            drawWindow(marker, mWindow)
            return mWindow
        }

        override fun getInfoContents(marker: Marker): View? {
            return null
        }

        private fun drawWindow(marker: Marker, view: View) {
            val userImginfo = mClickedClusterItemInfo
            val tvLatLong = view.findViewById<TextView>(R.id.info_popup_location_textview)
            val ivImg = view.findViewById<ImageView>(R.id.info_popup_imgview)

            tvLatLong.text = userImginfo.imageAddress
            Glide.with(context!!).load(userImginfo.imageDataPath).apply(RequestOptions().override(resources.getDimension(R.dimen.infowindow_img_width).toInt(), resources.getDimension(R.dimen.infowindow_img_height).toInt())).into(ivImg)
//            ivImg.setImageDrawable(Drawable.createFromPath(userImginfo.imageDataPath))
        }
    }
}