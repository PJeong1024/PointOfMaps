package com.example.pointofmaps.clusterlist

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.FeatureValues
import com.example.pointofmaps.classes.ImgFIleInterfaces
import com.google.android.gms.maps.model.LatLng

class ClusterItemDisplayFragments : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        val view: View = inflater.inflate(R.layout.activity_map_tab_img_display, container, false)
        val fileNameTv = view.findViewById<View>(R.id.tabed_img_filename) as TextView
        val locationTv = view.findViewById<View>(R.id.tabed_img_location) as TextView
        val loadImgIv = view.findViewById<View>(R.id.tabed_img_display_imageview) as ImageView
        val bundle = this.arguments
        if (bundle != null) {
            fileNameTv.text = bundle.getString(FeatureValues.IMG_FILENAME)
            if (Build.VERSION.SDK_INT >= 33) {
                locationTv.text = ImgFIleInterfaces().getImgAddrString(requireContext(), (bundle.getParcelable(FeatureValues.IMG_LOCATION, LatLng::class.java) as LatLng?)!!)
            } else {
                locationTv.text = ImgFIleInterfaces().getImgAddrString(requireContext(), (bundle.getParcelable(FeatureValues.IMG_LOCATION) as LatLng?)!!)
            }
            Glide.with(requireContext()).load(bundle.getString(FeatureValues.IMG_PATH)).into(loadImgIv)
        } else {
            Toast.makeText(context, R.string.string_falied_to_load_image, Toast.LENGTH_LONG).show()
            onDestroy()
        }
        return view
    }
}