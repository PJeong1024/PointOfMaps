package com.example.pointofmaps.classes

import android.location.Address
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

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
    @ColumnInfo var imageAddress: String = "") : ClusterItem, Parcelable {
    init {

    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readLong(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readString().toString()) {
    }

    fun getLatLong(): LatLng? {
        if (imageLat == 0.0 && imageLong == 0.0) {
            return null
        }
        return LatLng(imageLat!!, imageLong!!)
    }

    override fun getPosition(): LatLng {
        return getLatLong()!!

    }

    override fun getTitle(): String? {
        return imageDisplayName
    }

    override fun getSnippet(): String? {
        return imageID.toString()
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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(imageID)
        parcel.writeString(imageDataPath)
        parcel.writeString(imageDisplayName)
        parcel.writeValue(imageLat)
        parcel.writeValue(imageLong)
        parcel.writeLong(imageDateTaken)
        parcel.writeInt(imageOri)
        parcel.writeLong(imageSize)
        parcel.writeString(imageAddress)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserImg> {
        override fun createFromParcel(parcel: Parcel): UserImg {
            return UserImg(parcel)
        }

        override fun newArray(size: Int): Array<UserImg?> {
            return arrayOfNulls(size)
        }
    }

}