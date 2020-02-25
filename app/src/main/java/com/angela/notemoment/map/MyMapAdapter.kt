package com.angela.notemoment.map

import com.google.android.gms.maps.model.Marker
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.angela.notemoment.R
import com.google.android.gms.maps.GoogleMap


class CustomInfoWindowAdapter(private val context: Activity) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val view = context.layoutInflater.inflate(R.layout.item_map_info_window, null)

        return view
    }
}



//class MyMapAdapter(val context: Context) : GoogleMap.InfoWindowAdapter {
//
//    override fun getInfoWindow(marker: Marker): View? {
//
//        val window = (context as Activity).layoutInflater.inflate(R.layout.item_map_info_window, null)
//
//        return window
//    }
//
//    override fun getInfoContents(marker: Marker): View? {
//        return null
//    }
//
//
//
//}


