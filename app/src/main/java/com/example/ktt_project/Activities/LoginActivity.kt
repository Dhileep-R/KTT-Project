package com.example.ktt_project.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ktt_project.Models.DataModel
import com.example.ktt_project.R
import io.realm.Realm
import io.realm.kotlin.where

class LoginActivity : AppCompatActivity() {

    private lateinit var moveToSignUp:TextView
    private lateinit var realm:Realm
    private lateinit var editTextEmailLogin: EditText
    private lateinit var editTextPasswordLogin:EditText
    private lateinit var buttonLogin: Button

    private var dataModel = DataModel();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        editTextEmailLogin = findViewById(R.id.editTextEmailLogin)
        editTextPasswordLogin = findViewById(R.id.editTextPasswordLogin)
        buttonLogin = findViewById(R.id.buttonLogin)
        moveToSignUp = findViewById(R.id.moveToSignUp)

        realm = Realm.getDefaultInstance()

        buttonLogin.setOnClickListener{
             if (validateValues()){
                try{
                    val user = realm.where<DataModel>()
                        .equalTo("email", editTextEmailLogin.text.trim().toString())
                        .equalTo("password", editTextPasswordLogin.text.trim().toString())
                        .findFirst()

                    if (user != null){
                        editTextEmailLogin.text.clear()
                        editTextPasswordLogin.text.clear()
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.putExtra("USERID_EXTRA", user.id)


                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this, "Not Exist", Toast.LENGTH_SHORT).show()
                    }

                }catch (e:Exception){

                }
             }
             else {
                 Toast.makeText(this, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
             }
        }

        moveToSignUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateValues(): Boolean {

        val email = editTextEmailLogin.text.trim().toString()
        val password = editTextPasswordLogin.text.trim().toString()


        if (email.isEmpty() || password.isEmpty()) {
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmailLogin.error = "Invalid email"
            return false
        }

        if (password.length < 6) {
            editTextPasswordLogin.error = "Password must be at least 6 characters long"
            return false
        }
        return true
    }
}