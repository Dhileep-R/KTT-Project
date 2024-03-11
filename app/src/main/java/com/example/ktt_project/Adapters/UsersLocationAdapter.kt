package com.example.ktt_project.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ktt_project.Models.LocationData
import com.example.ktt_project.Activities.MapActivity
import com.example.ktt_project.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback


class UsersLocationAdapter(context:Context, private val valueList:List<LocationData>,): RecyclerView.Adapter<UsersLocationAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView),
        OnMapReadyCallback {

        val userLatitudeInfo:TextView = itemView.findViewById<TextView>(R.id.RecyclerViewItemLatitude)
        val userLongitudeInfo:TextView = itemView.findViewById<TextView>(R.id.RecyclerViewItemLongitude)

        override fun onMapReady(p0: GoogleMap) {
            TODO("Not yet implemented")
        }


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViews = LayoutInflater.from(parent.context).inflate(R.layout.user_locations_item_view,parent,false)
        return MyViewHolder(itemViews)
    }

    override fun getItemCount(): Int {
        if (valueList == null){
            return 0
        }
        else{
            return valueList.size
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = valueList[position]

        val longitudeText = SpannableString("Longitude: ${data.longitude}")
        longitudeText.setSpan(StyleSpan(Typeface.BOLD), 0, "Longitude: ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.userLongitudeInfo.text = longitudeText

        val latitudeText = SpannableString("Latitude: ${data.latitude}")
        latitudeText.setSpan(StyleSpan(Typeface.BOLD), 0, "Latitude: ".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        holder.userLatitudeInfo.text = latitudeText

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context,position.toString(),Toast.LENGTH_SHORT).show()
            val intent = Intent(holder.itemView.context, MapActivity::class.java)
            intent.putExtra("latitude", data.latitude)
            intent.putExtra("longitude", data.longitude)
            intent.putExtra("Position",position)
            holder.itemView.context.startActivity(intent)

            }
        }

}