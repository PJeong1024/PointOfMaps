package com.example.pointofmaps.clusterlist

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.UserImg
import com.example.pointofmaps.classes.FeatureValues

class TabClusterListDisplayActivity : AppCompatActivity() {
    private lateinit var mClickedClusterListInfo: ArrayList<UserImg>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab_cluster_list_display)
        val intent = intent

        if (intent != null) {
            Log.i(FeatureValues.AppName, "onCreate intent != null : ")
            mClickedClusterListInfo = if (Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableArrayListExtra(FeatureValues.IMG_ARRAYLIST, UserImg::class.java) ?: arrayListOf()
            } else {
                (intent.getParcelableExtra(FeatureValues.IMG_ARRAYLIST) as ArrayList<UserImg>?)!!
            }
            Log.i(FeatureValues.AppName, "onCreate mClickedClusterListInfo : " + mClickedClusterListInfo.size)
        } else {
            Toast.makeText(this, R.string.string_falied_to_load_image, Toast.LENGTH_LONG).show()
            onDestroy()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.cluster_items_listView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ClusterItemListViewAdapter(this, mClickedClusterListInfo)
        recyclerView.adapter = adapter
    }
}