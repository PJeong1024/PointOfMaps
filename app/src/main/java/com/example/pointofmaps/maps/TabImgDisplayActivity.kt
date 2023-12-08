package com.example.pointofmaps.maps

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.FeatureValues
import com.example.pointofmaps.classes.ImgFIleInterfaces
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class TabImgDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_tab_img_display)
        val fileNameTv = findViewById<View>(R.id.tabed_img_filename) as TextView
        val locationTv = findViewById<View>(R.id.tabed_img_location) as TextView
        val loadImgIv = findViewById<View>(R.id.tabed_img_display_imageview) as ImageView

        if (intent != null) {
            fileNameTv.text = intent.getStringExtra(FeatureValues.IMG_FILENAME)
            locationTv.text = intent.getStringExtra(FeatureValues.IMG_ADDRESS)
            Glide.with(this@TabImgDisplayActivity).load(intent.getStringExtra(FeatureValues.IMG_PATH)).into(loadImgIv)
        } else {
            Toast.makeText(this@TabImgDisplayActivity, R.string.string_falied_to_load_image, Toast.LENGTH_LONG).show()
            onDestroy()
        }
    }
}