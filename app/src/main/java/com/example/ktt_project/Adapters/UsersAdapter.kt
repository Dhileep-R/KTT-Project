package com.example.ktt_project.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.ktt_project.Models.DataModel
import com.example.ktt_project.R

class UsersAdapter(context: Context, resource: Int, objects: List<DataModel>) :
    ArrayAdapter<DataModel>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false)

        val user = getItem(position)
        val textViewUsername = view.findViewById<TextView>(R.id.textViewUsername)
        val textViewEmail = view.findViewById<TextView>(R.id.textViewEmail)


        // Set user details to the TextViews
        textViewUsername.text = user?.username
        textViewEmail.text = user?.email

        return view
    }
}