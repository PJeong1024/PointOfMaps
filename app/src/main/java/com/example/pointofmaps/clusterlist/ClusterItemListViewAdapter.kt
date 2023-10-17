package com.example.pointofmaps.clusterlist

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.pointofmaps.R
import com.example.pointofmaps.classes.UserImg
import com.example.pointofmaps.classes.FeatureValues

class ClusterItemListViewAdapter(context: Context?, cluster: ArrayList<UserImg>?) : RecyclerView.Adapter<ClusterItemListViewAdapter.ViewHolder>() {
    var mContext: Context? = null
    var mListItemsInfo: ArrayList<UserImg>? = null
    private var mLayoutInflater: LayoutInflater? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView
        var textview: TextView

        init {
            imageView = itemView.findViewById<View>(R.id.cluster_item_info_imgview) as ImageView
            textview = itemView.findViewById<View>(R.id.cluster_item_info_textview) as TextView
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    val intent = Intent(mContext, ClusterImgListHorizontalScrollActivity::class.java)
                    intent.putExtra(FeatureValues.IMG_ARRAYLIST, mListItemsInfo)
                    intent.putExtra(FeatureValues.IMG_POSITION_IN_ARRAYLIST, pos)
                    mContext!!.startActivity(intent)

                }
            }
        }
    }

    init {
        mContext = context
        mListItemsInfo = cluster
        mLayoutInflater = LayoutInflater.from(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = mContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.cluster_item_info_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val selectedItemInfo: UserImg = mListItemsInfo!![position]
        Glide.with(mContext!!).load(selectedItemInfo.imageDataPath).apply(RequestOptions().override(mContext!!.resources.getDimension(R.dimen.cluster_item_infowindow_img_width).toInt(), mContext!!.resources.getDimension(R.dimen.cluster_item_infowindow_img_height).toInt())).into(holder.imageView)
        holder.textview.text = selectedItemInfo.imageDisplayName
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mListItemsInfo!!.size
    }
}