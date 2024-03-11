package com.example.ktt_project.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ktt_project.Models.DataModel
import com.example.ktt_project.R
import io.realm.Realm
import io.realm.kotlin.where
import java.util.UUID


class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonSignUp: Button
    private lateinit var realm:Realm

    private var dataModel = DataModel();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonSignUp = findViewById(R.id.buttonSignUp)

        realm = Realm.getDefaultInstance()

        buttonSignUp.setOnClickListener {
            if (validateFields()) {
                val existingUser = realm.where<DataModel>().equalTo("email", editTextEmail.text.trim().toString()).findFirst()
                if (existingUser != null){
                    Toast.makeText(this, "Already Exist", Toast.LENGTH_SHORT).show()
                }
                else {
                    try {

                        realm.executeTransaction {
                            dataModel =
                                it.createObject(DataModel::class.java, UUID.randomUUID().toString())
                            dataModel.username = editTextUsername.text.trim().toString()
                            dataModel.email = editTextEmail.text.trim().toString()
                            dataModel.password = editTextConfirmPassword.text.trim().toString()

                        }
                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                        editTextUsername.text.clear()
                        editTextEmail.text.clear()
                        editTextPassword.text.clear()
                        editTextConfirmPassword.text.clear()
                        startActivity(Intent(this, LoginActivity::class.java))

                    } catch (e: Exception) {
                        println("error in db       " + e.message)
                        Toast.makeText(this, "Sign Up Unsuccessful", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateFields(): Boolean {
        val username = editTextUsername.text.toString().trim()
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val confirmPassword = editTextConfirmPassword.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Invalid email"
            return false
        }

        if (password.length < 6) {
            editTextPassword.error = "Password must be at least 6 characters long"
            return false
        }

        if (password != confirmPassword) {
            editTextConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }
}