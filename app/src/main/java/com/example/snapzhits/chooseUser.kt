package com.example.snapzhits

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class chooseUser : AppCompatActivity() {

    lateinit var chooseUserList:ListView
    lateinit var emails:ArrayList<String>
    lateinit var keys:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)

        chooseUserList=findViewById(R.id.chooseUserList)
        emails= ArrayList()
        keys= ArrayList()
        val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)

        chooseUserList.adapter=adapter

        FirebaseDatabase.getInstance().getReference().child("users").addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                  val email=p0.child("email").value as String
                  emails.add(email)
                  keys.add(p0.key.toString())
                Log.i("email dekho", emails.run { toString() })
                 adapter.notifyDataSetChanged()
            }
            override fun onChildRemoved(p0: DataSnapshot) {}

        })
        chooseUserList.onItemClickListener=AdapterView.OnItemClickListener { parent, view, position, id ->
            val snapMap:Map<String,String> = mapOf("from" to FirebaseAuth.getInstance().currentUser!!.email!!,"imageName" to intent.getStringExtra("imageName"),"imageURL" to intent.getStringExtra("imageURL"),"message" to intent.getStringExtra("message"))
             FirebaseDatabase.getInstance().getReference().child("users").child(keys.get(position)).child("snaps").push().setValue(snapMap)

            val intent=Intent(this,Snaps::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)  //deletes all the back button history ;)

            startActivity(intent)
        }
    }
}
