package com.example.pointofmaps.clusterlist

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pointofmaps.BuildConfig
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.UserImg
import com.example.pointofmaps.classes.FeatureValues
import com.google.android.gms.maps.model.LatLng

class ClusterImgListHorizontalScrollActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager2
    private var mClickedClusterListInfo: ArrayList<UserImg>? = null
    private var selectedImgPositon = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cluster_img_list_horizontal_scroll)
        val intent = intent
        if (intent != null) {
            mClickedClusterListInfo = if (Build.VERSION.SDK_INT >= 33) {
                (intent.getSerializableExtra(FeatureValues.IMG_ARRAYLIST, UserImg::class.java) as ArrayList<UserImg>?)!!
            } else {
                (intent.getSerializableExtra(FeatureValues.IMG_ARRAYLIST) as ArrayList<UserImg>?)!!
            }
            selectedImgPositon = intent.getIntExtra(FeatureValues.IMG_POSITION_IN_ARRAYLIST, 0)
        } else {
            Toast.makeText(this, R.string.string_falied_to_load_image, Toast.LENGTH_LONG).show()
            onDestroy()
        }
        mPager = findViewById(R.id.cluster_item_detail_viewpager)
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter
        mPager.currentItem = selectedImgPositon
    }

    inner class ScreenSlidePagerAdapter(fm: ClusterImgListHorizontalScrollActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = mClickedClusterListInfo!!.size

        override fun createFragment(position: Int): Fragment {
            val imgInfoStrings: UserImg = mClickedClusterListInfo!![position]
            val fragment = ClusterItemDisplayFragments()
            val bundle = Bundle()
            bundle.putInt(FeatureValues.IMG_ID, imgInfoStrings.imageID)
            bundle.putString(FeatureValues.IMG_PATH, imgInfoStrings.imageDataPath)
            bundle.putString(FeatureValues.IMG_FILENAME, imgInfoStrings.imageDisplayName)
            bundle.putParcelable(FeatureValues.IMG_LOCATION, LatLng(imgInfoStrings.imageLat!!, imgInfoStrings.imageLong!!) )
            bundle.putInt(FeatureValues.IMG_ORIENTATION, imgInfoStrings.imageOri)
            fragment.arguments = bundle
            return fragment
        }
    }
}