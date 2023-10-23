package com.example.pointofmaps

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.pointofmaps.addr.ImgAddrFragment
import com.example.pointofmaps.classes.*
import com.example.pointofmaps.maps.ImgMapFragment
import com.example.pointofmaps.translate.TranslateFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mCoroutineScope: CoroutineScope
    private lateinit var mCoroutineJob: Job
    private lateinit var mDao: UserImgDao

    private val NUM_PAGES = 3
    private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1000
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1001
    private val PERMISSIONS_REQUEST_ACCESS_MEDIA_LOCATION = 1002

    private lateinit var mPager: ViewPager2
    private lateinit var mLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        mDao = ImgMapDatabase.getDatabase(this@MainActivity).userDao()

        if (isAllPermissionIsAgreed()) {
            refreshImgListToDB()
            updateImgAddrToDB()
            displayScreen()
        }
    }

    private fun refreshImgListToDB() {
        mCoroutineJob = mCoroutineScope.launch(Dispatchers.IO) {
            Log.i(FeatureValues.AppName, "Dispatchers.IO : start")
            val wholeImgList = ImgFIleInterfaces().ReadImgListFromStorage(this@MainActivity)!!
            wholeImgList.sortByDescending { userImg -> userImg.imageDateTaken }
            for (userImg in wholeImgList) {
                if (!userImg.imageDisplayName.contains(".jpg")) {
//                    Log.i(FeatureValues.AppName, "not a JPEG image format : " + userImg.imageDisplayName)
                    continue
                }

                val locatedImg = ImgFIleInterfaces().ReadExifDataFromImgFilesUnderQ(this@MainActivity, userImg)
                if (locatedImg.getLatLong() == null) {
//                    Log.i(FeatureValues.AppName, "image do not have LatLng : " + userImg.imageDisplayName)
                    continue
                }

                var foundImgFromDB = UserImg()
                foundImgFromDB = mDao.findByName(locatedImg.imageID, locatedImg.imageDisplayName)

                if (foundImgFromDB == null) {
                    mDao.insert(locatedImg)
                } else if (foundImgFromDB.isSameImg(locatedImg)) {
//                    Log.i(FeatureValues.AppName, "image already in DB : " + userImg.imageDisplayName)
                    continue
                } else {
                    mDao.insert(locatedImg)
                }
            }
        }
        Log.i(FeatureValues.AppName, "Dispatchers.IO : end")
    }

    private fun updateImgAddrToDB() {
        mCoroutineJob = mCoroutineScope.launch(Dispatchers.Default) {
            Log.i(FeatureValues.AppName, "Dispatchers.Default : start")
            var limitedRequest = 0
            val queueItems: Queue<UserImg> = LinkedList()
            queueItems.addAll(mDao.getImagesWithoutLocation())
            Log.i(FeatureValues.AppName, "queueItems : " + queueItems.size)
            while (queueItems.isNotEmpty()) {
                if (limitedRequest > 100) {
                    continue
                }

                val targetItem = queueItems.poll()
                limitedRequest++
                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                try {
                    if (Build.VERSION.SDK_INT >= 33) {
                        geocoder.getFromLocation(targetItem!!.imageLat!!, targetItem.imageLong!!, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addrs: MutableList<Address>) {
                                Log.i(FeatureValues.AppName, "onGeocode : " + addrs[0].countryName)
                                limitedRequest--
                                targetItem.imageCountryName = addrs[0].countryName
                                mDao.update(targetItem)
                            }

                            override fun onError(errorMessage: String?) {
                                Log.i(FeatureValues.AppName, "onError")
                                super.onError(errorMessage)
                                queueItems.add(targetItem)
                            }
                        })
                    } else {
                        Log.i(FeatureValues.AppName, "Build.VERSION.SDK_INT < 33")
                        val addresses = geocoder.getFromLocation(targetItem!!.imageLat!!, targetItem.imageLong!!, 5)!!
                        Log.i(FeatureValues.AppName, "addresses : $addresses")
                        targetItem.imageCountryName = addresses[0].countryName
                        mDao.update(targetItem)
                        limitedRequest--
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.i(FeatureValues.AppName, "limitedRequest : $limitedRequest")
            }

            Log.i(FeatureValues.AppName, "Dispatchers.Default : end")
        }
    }

    private fun displayScreen() {
        setContentView(R.layout.activity_mainscreen)
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.main_activity_pager)
        mPager.isUserInputEnabled = false

        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter

        // init TabLayout
        mLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(mLayout, mPager) { tab, position ->
            when (position) {
                0    -> tab.text = getString(R.string.tab_name_pictures_by_map)
                1    -> tab.text = getString(R.string.tab_name_text_translation)
                2    -> tab.text = getString(R.string.tab_name_pictures_by_address)
                else -> tab.text = "-"
            }
        }.attach()
    }

//    override fun onPause() {
//        super.onPause()
//        mCoroutineJob.cancel()
//        mCoroutineScope.cancel()
//    }

    override fun onDestroy() {
        super.onDestroy()
        mCoroutineJob.cancel()
        mCoroutineScope.cancel()
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0    -> ImgMapFragment()
                1    -> TranslateFragment()
                2    -> ImgAddrFragment()
                else -> ImgAddrFragment()
            }
        }
    }

    private fun isAllPermissionIsAgreed(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
            return false
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            return false
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_MEDIA_LOCATION), PERMISSIONS_REQUEST_ACCESS_MEDIA_LOCATION)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isAllPermissionIsAgreed()) {
                    displayScreen()
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Should add permission guide flow here
                    Toast.makeText(this, R.string.string_grant_permission_storage, Toast.LENGTH_LONG).show()
                }
                ActivityCompat.finishAffinity(this)
            }
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION  -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isAllPermissionIsAgreed()) {
                    displayScreen()
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Should add permission guide flow here
                    Toast.makeText(this, R.string.string_grant_permission_location, Toast.LENGTH_LONG).show()
                }
                ActivityCompat.finishAffinity(this)
            }
            PERMISSIONS_REQUEST_ACCESS_MEDIA_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isAllPermissionIsAgreed()) {
                    displayScreen()
                }
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_MEDIA_LOCATION)) {
                    // Should add permission guide flow here
                    Toast.makeText(this, R.string.string_grant_permission_media_location, Toast.LENGTH_LONG).show()
                }
                ActivityCompat.finishAffinity(this)
            }
            else                                      -> {
                Toast.makeText(this, R.string.string_without_permission_guide, Toast.LENGTH_LONG).show()
                ActivityCompat.finishAffinity(this)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}