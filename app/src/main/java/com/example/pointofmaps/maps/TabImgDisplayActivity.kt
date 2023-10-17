package com.example.pointofmaps.maps

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.FeatureValues
import com.example.pointofmaps.classes.ImgFIleInterfaces
import com.google.android.gms.maps.model.LatLng

class TabImgDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_tab_img_display)
        val fileNameTv = findViewById<View>(R.id.tabed_img_filename) as TextView
        val locationTv = findViewById<View>(R.id.tabed_img_location) as TextView
        val loadImgIv = findViewById<View>(R.id.tabed_img_display_imageview) as ImageView
        val intent = intent
        if (intent != null) {
            fileNameTv.text = intent.getStringExtra(FeatureValues.IMG_FILENAME)
            if (Build.VERSION.SDK_INT >= 33) {
                locationTv.text = ImgFIleInterfaces().getImgAddrString(this, (intent.getParcelableExtra(FeatureValues.IMG_LOCATION, LatLng::class.java) as LatLng?)!!)
            } else {
                locationTv.text = ImgFIleInterfaces().getImgAddrString(this, (intent.getParcelableExtra<Parcelable>(FeatureValues.IMG_LOCATION) as LatLng?)!!)
            }
            Glide.with(this).load(intent.getStringExtra(FeatureValues.IMG_PATH)).into(loadImgIv)
        } else {
            Toast.makeText(this, R.string.string_falied_to_load_image, Toast.LENGTH_LONG).show()
            onDestroy()
        }
    }
}