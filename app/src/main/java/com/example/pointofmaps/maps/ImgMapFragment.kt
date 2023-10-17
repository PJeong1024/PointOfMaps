package com.example.pointofmaps.maps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.*


class ImgMapFragment : Fragment(), OnMapReadyCallback, OnClusterClickListener<UserImg>, OnClusterItemClickListener<UserImg>, OnClusterItemInfoWindowClickListener<UserImg> {

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
        mMap.setOnInfoWindowClickListener(mClusterManager)
        mMap.setInfoWindowAdapter(mClusterManager.markerManager)

        mLatLongImgList = ArrayList<UserImg>()
        mClickedClusterItemInfo = UserImg()
        mClusterManager.markerCollection.setInfoWindowAdapter(ClusterItemInfoWindowAdapter(context))

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
//        mCoroutineScope.launch(Dispatchers.IO) {
//            Log.i(FeatureValues.AppName, "Dispatchers.IO : start")
//            val wholeImgList = ImgFIleInterfaces().ReadImgListFromStorage(context)!!
//            wholeImgList.sortByDescending { userImg -> userImg.imageDateTaken }
//            Log.i(FeatureValues.AppName, "wholeImgList size : " + wholeImgList.size)
//            Log.i(FeatureValues.AppName, "wholeImgList first : " + wholeImgList[0].toString())
//            Log.i(FeatureValues.AppName, "wholeImgList last : " + wholeImgList[wholeImgList.size - 1].toString())
//            for (userImg in wholeImgList) {
//                if (!userImg.imageDisplayName.contains(".jpg")) {
//                    continue
//                }
//                val locatedImg = ImgFIleInterfaces().ReadExifDataFromImgFiles(context, userImg)
//                if (locatedImg.getLatLong() != null) {
//                    mLatLongImgList.add(locatedImg)
//                    withContext(Dispatchers.Main) {
//                        mClusterManager.addItem(locatedImg)
//                    }
//                }
//            }
//
//            withContext(Dispatchers.Main) {
//                val latLng = mLatLongImgList[0].getLatLong()
//                Log.i(FeatureValues.AppName, "last image : " + mLatLongImgList[0].imageDataPath)
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, 15f))
//            }
//            Log.i(FeatureValues.AppName, "Dispatchers.IO : end")
//        }


        Log.i(FeatureValues.AppName, "onMapReady : end")
    }

//    fun readAndDrawImgsInMap(): ArrayList<UserImg> {
//        Log.i(FeatureValues.AppName, "readAndDrawImgsInMap")
//        val wholeImgList = ImgFIleInterfaces().ReadImgListFromStorage(context)!!
//        val latLongImgList = ArrayList<UserImg>()
//        var index = wholeImgList.size + 1
//        while (index >= 0) {
//            val userImg = ImgFIleInterfaces().ReadExifDataFromImgFiles(context, wholeImgList.get(index))
//            if (userImg.imageLatLong != null) {
//                latLongImgList.add(userImg)
//            }
//            index--
//        }
//
//        return latLongImgList
//    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun reDrawClusterItems() {
        mClusterManager.addItems(mLatLongImgList)
        mMap.setOnCameraIdleListener(mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)
        mMap.setOnInfoWindowClickListener(mClusterManager)
        mMap.setInfoWindowAdapter(mClusterManager.markerManager)

        mClusterManager.setOnClusterClickListener(this)
        mClusterManager.setOnClusterItemClickListener(this)
        mClusterManager.setOnClusterItemInfoWindowClickListener(this)
//        mClusterManager.markerCollection.setOnInfoWindowAdapter(ClusterItemInfoWindowAdapter(context))
    }

    private fun reDrawClusterItem(imgLatLongInfo: UserImg) {
        mClusterManager.addItem(imgLatLongInfo)
        mClusterManager.setOnClusterClickListener(this)
        mClusterManager.setOnClusterItemClickListener(this)
        mClusterManager.setOnClusterItemInfoWindowClickListener(this)
//        mClusterManager.markerCollection.setOnInfoWindowAdapter(ClusterItemInfoWindowAdapter(context))
        mClusterManager.cluster()
    }

    override fun onClusterClick(cluster: Cluster<UserImg>?): Boolean {
        Log.i(FeatureValues.AppName, "onClusterClick")
        mClickedClusterListInfo = ArrayList<UserImg>()
        for (info in cluster!!.items) {
            mClickedClusterListInfo.add(info)
//            mClusterManager.cluster()
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
        intent.putExtra(FeatureValues.IMG_LOCATION, LatLng(userImg.imageLat!!, userImg.imageLong!!) )
        intent.putExtra(FeatureValues.IMG_ORIENTATION, userImg.imageOri)
        startActivity(intent)
    }

    inner class ClusterItemInfoWindowAdapter(context: Context?) : GoogleMap.InfoWindowAdapter {
        private var mContext = context
        private var mWindow = (context as Activity).layoutInflater.inflate(R.layout.info_popup_layout, null)

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View {
            val userImginfo = mClickedClusterItemInfo
            val v: View = mWindow
            val ivImg = v.findViewById<ImageView>(R.id.info_popup_imgview)
            val tvLatLong = v.findViewById<TextView>(R.id.info_popup_location_textview)

            tvLatLong.text = ImgFIleInterfaces().getImgAddrString(mContext!!, userImginfo.getLatLong()!!)!!
            Glide.with(mContext!!).load(userImginfo.imageDataPath).apply(RequestOptions().override(mContext!!.resources.getDimension(R.dimen.infowindow_img_width).toInt(), mContext!!.resources.getDimension(R.dimen.infowindow_img_height).toInt())).into(ivImg)

            return v
        }
    }
}