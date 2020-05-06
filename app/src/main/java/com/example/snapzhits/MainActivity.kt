package com.example.snapzhits

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Patterns.EMAIL_ADDRESS
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern
import java.util.regex.Pattern.matches

class MainActivity : AppCompatActivity() {

    lateinit var username: EditText
    lateinit var password: EditText
    lateinit var eml: String
    lateinit var pass: String
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance().getReference("users")
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
    }

    override fun onStart() {
        super.onStart()
       if(firebaseAuth.currentUser!=null)
           startActivity(Intent(this,Snaps::class.java))
    }
    fun regex(view: View) {
        eml = username.text.toString().trim()
        pass = password.text.toString().trim()

//        Toast.makeText(this, "Successfully logged in"+eml+pass, Toast.LENGTH_SHORT).show()


        if (!EMAIL_ADDRESS.matcher(eml).matches()) {
            username.setError("enter a valid email")
            username.requestFocus()
            return
        }
        if (pass.length < 6) {
            password.setError("minimum length is 6")
            password.requestFocus()
            return
        }
        firebaseAuth.signInWithEmailAndPassword(eml, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully logged in", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, Snaps::class.java))
                } else {

                    firebaseAuth.createUserWithEmailAndPassword(eml, pass)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Successfully registered, now log in",
                                    Toast.LENGTH_SHORT
                                ).show()
                                firebaseDatabase.child(firebaseAuth.uid.toString()).child("email").setValue(eml)
                            } else {
                                Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                }
            }

    }
}