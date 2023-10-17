package com.example.pointofmaps.maps

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.pointofmaps.classes.DataRepository
import com.example.pointofmaps.classes.ImgMapDatabase
import com.example.pointofmaps.classes.UserImg

class MapClusterViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: DataRepository
    private lateinit var UserImgListLiveData: LiveData<List<UserImg>>
    private lateinit var UserImgLiveData: LiveData<UserImg>

    init {
        repository = DataRepository(ImgMapDatabase.getDatabase(application).userDao())
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun getAllImgClusters() : LiveData<List<UserImg>> {
        UserImgListLiveData = repository.allProducts
        return UserImgListLiveData
    }
}