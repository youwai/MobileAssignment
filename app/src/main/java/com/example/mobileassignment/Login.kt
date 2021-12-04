package com.example.mobileassignment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import com.example.mobileassignment.databinding.ActivityLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db = Firebase.firestore
    private var selectedEmp: Employee? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.loginBtn.setOnClickListener {
            val id = binding.idInput.text.toString()
            val pass = binding.passInput.text.toString()

            verifyLogin(id, pass, object : FirestoreCallback {
                override fun onCallback(item: Employee?) {
                    Log.d("check", selectedEmp.toString())
                }
            })
        }

        binding.idInput.setOnClickListener {
            binding.idInputLayout.error = null
        }

        binding.idInput.doOnTextChanged { _, _, _, _ ->
            binding.idInputLayout.error = null
        }

        binding.passInput.setOnClickListener {
            binding.passInputLayout.error = null
            binding.passInput.text?.clear()
        }

    }

    private fun verifyLogin (id: String, pass: String, firestoreCallback: FirestoreCallback) {
        db.collection("Employee")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (id == document.data["id"].toString() && pass == document.data["pass"].toString()) {
                        selectedEmp = Employee(document.data["name"].toString(), document.data["id"].toString())
                        break
                    }
                }
                firestoreCallback.onCallback(selectedEmp)
                getIn()
            }
            .addOnFailureListener { exception ->
                Log.w("failedAttempt", "Error getting documents.", exception)
            }
    }

    private fun getIn () {
        val intent = Intent(this, MainActivity::class.java)

        if (selectedEmp == null) {
            Toast.makeText(this, "Invalid login info", Toast.LENGTH_LONG).show()
            binding.idInputLayout.error = " "
            binding.passInputLayout.error = " "
        }
        else {
            intent.putExtra("user", selectedEmp)
            startActivity(intent)
            finish()
        }
    }

//    override fun onRestart() {
//        super.onRestart()
//        selectedEmp = null
//    }

    private interface FirestoreCallback {
        fun onCallback(item: Employee?)
    }
}