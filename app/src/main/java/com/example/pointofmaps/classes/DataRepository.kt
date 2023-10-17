package com.example.pointofmaps.classes

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataRepository(private val userImgDao: UserImgDao) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    val allProducts: LiveData<List<UserImg>> = userImgDao.getAll()

    fun insertUserImg(userImg: UserImg) {
        coroutineScope.launch(Dispatchers.IO) {
            userImgDao.insert(userImg)
        }
    }
}