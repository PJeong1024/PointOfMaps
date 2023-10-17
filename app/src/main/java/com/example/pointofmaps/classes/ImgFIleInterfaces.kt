package com.example.pointofmaps.classes

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.util.*

private val PROJECTION_FORMAT =
    arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.SIZE)

class ImgFIleInterfaces {

    init {

    }

    fun ReadImgListFromStorage(mContext: Context?): ArrayList<UserImg>? {
        Log.i(FeatureValues.AppName, "ReadImgFilesFromStorage")
        val mImgList: ArrayList<UserImg> = ArrayList<UserImg>()
        val cursor = mContext?.contentResolver?.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_FORMAT, null, null, null)

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

        return mImgList
    }

//    fun ReadImgListWithLatLongFromStorage(mContext: Context?): ArrayList<UserImg>? {
//        Log.i(FeatureValues.AppName, "ReadImgListWithLatLongFromStorage")
//        val mImgList: ArrayList<UserImg> = ArrayList<UserImg>()
//        val cursor = mContext?.contentResolver?.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_FORMAT, null, null, null)
//
//        if (cursor != null) {
//            val imageIDCol: Int = cursor.getColumnIndex(MediaStore.Images.Media._ID)
//            val imageDataCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
//            val imageNameCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
//            val imageDateTakenCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
//            val imageImgOrientation: Int = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION)
//            val imageSizeCol: Int = cursor.getColumnIndex(MediaStore.Images.Media.SIZE)
//            while (cursor.moveToNext()) {
//                var readImg = UserImg()
//                readImg.imageID = cursor.getInt(imageIDCol)
//                readImg.imageDataPath = cursor.getString(imageDataCol)
//                readImg.imageDisplayName = cursor.getString(imageNameCol)
//                readImg.imageDateTaken = cursor.getLong(imageDateTakenCol)
//                readImg.imageOri = cursor.getInt(imageImgOrientation)
//                readImg.imageSize = cursor.getLong(imageSizeCol)
//
//                readImg = ReadExifDataFromImgFiles(mContext, readImg)
//
//                if (readImg.getLatLong() != null) {
//                    mImgList.add(readImg)
//                }
//            }
//        } else {
//            Log.i(FeatureValues.AppName, "no image files here")
//        }
//
//        return mImgList
//    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun ReadExifDataFromImgFiles(mContext: Context?, userImg: UserImg): UserImg {
        var photoUri: Uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, userImg.imageID.toString())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        photoUri = MediaStore.setRequireOriginal(photoUri)
        mContext?.contentResolver?.openInputStream(photoUri)?.use { stream ->
            try {
                val exif = ExifInterface(stream)
                val latLong = FloatArray(2)
                if (exif.getLatLong(latLong)) {
                    userImg.imageLat = latLong[0].toDouble()
                    userImg.imageLong = latLong[1].toDouble()
//                    userImg.imageCountryName = getAddressStringByGeocoder(mContext, userImg.getLatLong()!!)[0].countryName
                } else {
                    userImg.imageLat = 0.0
                    userImg.imageLong = 0.0
                }
            } catch (e: IOException) {
                Log.i(FeatureValues.AppName, "no GPS values in image : " + userImg.imageDisplayName)
            }
        }
//        }
        return userImg
    }

    @SuppressLint("NewApi")
    fun ReadExifDataFromImgFilesUnderQ(mContext: Context?, userImg: UserImg): UserImg {
        var photoUri: Uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, userImg.imageID.toString())
        photoUri = MediaStore.setRequireOriginal(photoUri)
        mContext?.contentResolver?.openInputStream(photoUri)?.use { stream ->
            try {
                val exif = ExifInterface(stream)
                val latLong = FloatArray(2)
                if (exif.getLatLong(latLong)) {
                    userImg.imageLat = latLong[0].toDouble()
                    userImg.imageLong = latLong[1].toDouble()
//                    val addresses: List<Address>? = getAddressStringByGeocoder(mContext, userImg.getLatLong()!!)
//                    if (!addresses.isNullOrEmpty()) {
//                        userImg.imageCountryName = addresses[0].countryName
//                    } else {
//                        userImg.imageCountryName = null
//                    }
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

    fun getImgAddrString(mContext: Context, ll: LatLng): String? {
        var locationName: String? = null
        val addrList: List<Address> = getAddressStringByGeocoder(mContext, ll)
        locationName = if (addrList.isNotEmpty()) {
            addrList[0].locality + ", " + addrList[0].adminArea + ", " + addrList[0].countryName
        } else {
            ll.toString()
        }
        return locationName
    }

    fun getImgAddrArray(mContext: Context, ll: LatLng): List<Address> {
        return getAddressStringByGeocoder(mContext, ll)
    }

    private fun getAddressStringByGeocoder(c: Context, latlong: LatLng): List<Address> {
        var addresses: List<Address>? = null
        val geocoder = Geocoder(c, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= 33) {
                geocoder.getFromLocation(latlong.latitude, latlong.longitude, 1, object : GeocodeListener{
                    override fun onGeocode(addrs: MutableList<Address>) {
                        Log.i(FeatureValues.AppName, "onGeocode : " + addrs[0].countryName)
                        addresses = addrs
                    }

                    override fun onError(errorMessage: String?) {
                        Log.i(FeatureValues.AppName, "onError")
                        super.onError(errorMessage)
                        addresses = null
                    }

                })
            } else {
                addresses = geocoder.getFromLocation(latlong.latitude, latlong.longitude, 1)!!
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addresses!!
    }
}

