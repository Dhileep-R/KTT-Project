package com.example.ktt_project.PopUpWindows

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.PopupWindow
import com.example.ktt_project.Interfaces.OnUserSelectedListener
import com.example.ktt_project.Adapters.UsersAdapter
import com.example.ktt_project.Models.DataModel
import com.example.ktt_project.R
import io.realm.Realm

class UsersPopup(context: Context,private val onUserSelectedListener: OnUserSelectedListener) : PopupWindow(context) {

    private lateinit var realm:Realm

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.popup_users, null)

        contentView = view
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isFocusable = true

        val listViewUsers = view.findViewById<ListView>(R.id.listViewUsers)
        val usersAdapter = UsersAdapter(context, R.layout.list_item_user, getUsersList())
        listViewUsers.adapter = usersAdapter

        listViewUsers.setOnItemClickListener { parent, view, position, id ->
            val selectedUser = usersAdapter.getItem(position)
            if (selectedUser != null) {
                onUserSelectedListener.onUserSelected(selectedUser)
                dismiss()
            }
        }

        val buttonClose = view.findViewById<Button>(R.id.buttonClose)
        buttonClose.setOnClickListener { dismiss() }
    }

    private fun getUsersList(): List<DataModel> {
        realm = Realm.getDefaultInstance()
        val userslist : List<DataModel> = realm.where(DataModel::class.java).findAll()
        return userslist
    }
}




