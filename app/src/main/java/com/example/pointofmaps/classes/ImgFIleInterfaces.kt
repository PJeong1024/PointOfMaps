package com.example.pointofmaps.classes

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.*
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

private val PROJECTION_FORMAT =
    arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.SIZE)

class ImgFIleInterfaces {

    fun readImgListFromStorage(mContext: Context): ArrayList<UserImg> {
        Log.i(FeatureValues.AppName, "ReadImgFilesFromStorage")
        val mImgList: ArrayList<UserImg> = ArrayList<UserImg>()
        val cursor = mContext.contentResolver?.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_FORMAT, null, null, null)

        if (cursor != null) {
            val imageIDCol: Int = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val imageDataCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val imageNameCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
            val imageDateTakenCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
            val imageImgOrientation: Int = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)
            val imageSizeCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
            while (cursor.moveToNext()) {
                val readImg = UserImg()
                readImg.imageID = cursor.getInt(imageIDCol)
                readImg.imageDataPath = cursor.getString(imageDataCol)
                readImg.imageDisplayName = cursor.getString(imageNameCol)
                readImg.imageDateTaken = cursor.getLong(imageDateTakenCol)
                readImg.imageOri = cursor.getInt(imageImgOrientation)
                readImg.imageSize = cursor.getLong(imageSizeCol)

                if (readImg.imageDateTaken == 0L) {
                    continue
                }

                mImgList.add(readImg)
            }
        } else {
            Log.i(FeatureValues.AppName, "no image files here")
        }

        cursor?.close()
        return mImgList
    }

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    fun readExifDataFromImgFilesUnderQ(mContext: Context, userImg: UserImg): UserImg {
        var photoUri: Uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, userImg.imageID.toString())
        photoUri = MediaStore.setRequireOriginal(photoUri)
        mContext.contentResolver?.openInputStream(photoUri)?.use { stream ->
            try {
                val exif = ExifInterface(stream)
                val latLong = FloatArray(2)
                if (exif.getLatLong(latLong)) {
                    userImg.imageLat = latLong[0].toDouble()
                    userImg.imageLong = latLong[1].toDouble()
                } else {
                    userImg.imageLat = 0.0
                    userImg.imageLong = 0.0
                }
            } catch (e: IOException) {
                Log.i(FeatureValues.AppName, "no GPS values in image : " + userImg.imageDisplayName)
            }
        }
        return userImg
    }

//    suspend fun getImgAddrString(mContext: Context, ll: LatLng): String {
//        lateinit var locationName: String
//
//        val addrList: List<Address> = getAddressStringByGeocoder(mContext, ll)
//        locationName = if (addrList.isNotEmpty()) {
//            addrList[0].locality + ", " + addrList[0].adminArea + ", " + addrList[0].countryName
//        } else {
//            ll.toString()
//        }
//        return locationName
//    }
//
//    suspend fun getImgAddrStringUnderQ(mContext: Context, ll: LatLng): String {
//        lateinit var locationName: String
//
//        val addrList: List<Address> = getAddressStringByGeocoder(mContext, ll)
//        locationName = if (addrList.isNotEmpty()) {
//            addrList[0].locality + ", " + addrList[0].adminArea + ", " + addrList[0].countryName
//        } else {
//            ll.toString()
//        }
//        return locationName
//    }

    @Suppress("DEPRECATION")
    private suspend fun getAddressStringByGeocoder(c: Context, latlong: LatLng): List<Address> {
        return withContext(Dispatchers.IO) {
            var addresses: List<Address>? = null
            val geocoder = Geocoder(c, Locale.getDefault())
            try {
                if (Build.VERSION.SDK_INT >= 33) {
                    geocoder.getFromLocation(latlong.latitude, latlong.longitude, 1, object : GeocodeListener{
                        override fun onGeocode(addrs: List<Address>) {
                            Log.i(FeatureValues.AppName, "onGeocode : " + addrs[0].countryName)
                            addresses = addrs

                        }

                        override fun onError(errorMessage: String?) {
                            Log.i(FeatureValues.AppName, "onError")
                            super.onError(errorMessage)
                            addresses = null
                        }

                    })
                    Log.i(FeatureValues.AppName, "addresses : " + addresses?.size)
                    return@withContext addresses!!
                } else {
                    addresses = geocoder.getFromLocation(latlong.latitude, latlong.longitude, 1)!!
                    Log.i(FeatureValues.AppName, "addresses : " + addresses?.size)
                    return@withContext addresses!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }
        }
    }
}

