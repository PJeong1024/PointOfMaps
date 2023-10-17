package com.example.pointofmaps.classes

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.pointofmaps.classes.UserImg

@Dao
interface UserImgDao {
    @Query("SELECT * FROM UserImg")
    fun getAll(): LiveData<List<UserImg>>

    @Query("SELECT * FROM UserImg WHERE imageID IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<UserImg>

    @Query("SELECT * FROM UserImg WHERE imageID LIKE :imgID AND imageDisplayName LIKE :imgName LIMIT 1")
    fun findByName(imgID: Int, imgName: String): UserImg

    @Query("SELECT * FROM UserImg WHERE imageCountryName IS NULL")
    fun getImagesWithoutLocation(): List<UserImg>

    @Insert
    fun insert(userImg: UserImg)

    @Update
    fun update(userImg: UserImg)

    @Delete
    fun delete(userImg: UserImg)
}