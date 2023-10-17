package com.example.pointofmaps.classes

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import java.io.Serializable

@Entity
data class UserImg(
    @PrimaryKey @ColumnInfo var imageID: Int = 0,
    @ColumnInfo var imageDataPath: String = "",
    @ColumnInfo var imageDisplayName: String = "",
    @ColumnInfo var imageLat: Double? = 0.0,
    @ColumnInfo var imageLong: Double? = 0.0,
    @ColumnInfo var imageDateTaken: Long = 0L,
    @ColumnInfo var imageOri: Int = 0,
    @ColumnInfo var imageSize: Long = 0L,
    @ColumnInfo var imageCountryName: String? = null) : ClusterItem, Serializable {
    init {

    }

//    constructor(parcel: Parcel) : this() {
//        imageID = parcel.readString().toString()
//        imageDataPath = parcel.readString().toString()
//        imageDisplayName = parcel.readString().toString()
//        imageLatLong = parcel.readParcelable(LatLng::class.java.classLoader)
//        imageDateTaken = parcel.readString()
//        imageOri = parcel.readInt()
//        imageSize = parcel.readString()
//        imageCountryName = parcel.readString()
//    }

    fun getLatLong(): LatLng? {
        if (imageLat == 0.0 && imageLat == 0.0) {
            return null
        }
        return LatLng(imageLat!!, imageLong!!)
    }

    override fun getPosition(): LatLng {
        return getLatLong()!!

    }

    override fun getTitle(): String? {
        return null
    }

    override fun getSnippet(): String? {
        return null
    }

    fun isEnabled(): Boolean {
        if (imageID > 0 || imageDisplayName == "" || imageDataPath == "" || getLatLong() == null) {
            return false
        }

        return true
    }

    fun isSameImg(img : UserImg) : Boolean {
        if (img.imageID == imageID
            && img.imageDisplayName == imageDisplayName
            && img.imageDataPath == imageDataPath
            && img.imageDateTaken == imageDateTaken) {
            return true
        }

        return false
    }

//    override fun describeContents(): Int {
//        return this.hashCode()
//    }

//    override fun writeToParcel(dest: Parcel, flags: Int) {
//        dest.writeString(imageID)
//        dest.writeString(imageDataPath)
//        dest.writeString(imageDisplayName)
//        dest.writeParcelable(imageLatLong, flags)
//        dest.writeString(imageDateTaken)
//        dest.writeInt(imageOri)
//        dest.writeString(imageSize)
//        dest.writeString(imageCountryName)
//    }

//    companion object CREATOR : Parcelable.Creator<UserImg> {
//        override fun createFromParcel(parcel: Parcel): UserImg {
//            return UserImg(parcel)
//        }
//
//        override fun newArray(size: Int): Array<UserImg?> {
//            return arrayOfNulls(size)
//        }
//    }

}