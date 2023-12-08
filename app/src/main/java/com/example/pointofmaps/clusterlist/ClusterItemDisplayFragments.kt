package com.example.pointofmaps.clusterlist

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.FeatureValues
import com.example.pointofmaps.classes.ImgFIleInterfaces
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class ClusterItemDisplayFragments : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        val view: View = inflater.inflate(R.layout.activity_map_tab_img_display, container, false)
        val fileNameTv = view.findViewById<View>(R.id.tabed_img_filename) as TextView
        val locationTv = view.findViewById<View>(R.id.tabed_img_location) as TextView
        val loadImgIv = view.findViewById<View>(R.id.tabed_img_display_imageview) as ImageView
        val bundle = this.arguments

        lifecycleScope.launch {
            if (bundle != null) {
                fileNameTv.text = bundle.getString(FeatureValues.IMG_FILENAME)
                locationTv.text = bundle.getString(FeatureValues.IMG_ADDRESS)
                Glide.with(requireContext()).load(bundle.getString(FeatureValues.IMG_PATH)).into(loadImgIv)
            } else {
                Toast.makeText(context, R.string.string_falied_to_load_image, Toast.LENGTH_LONG).show()
                onDestroy()
            }
        }

        return view
    }
}