package com.example.snapzhits

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_snaps.*

class Snaps : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var snapsListView: ListView
    lateinit var emails:ArrayList<String>
    lateinit var snaps:ArrayList<DataSnapshot>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snaps)
        firebaseAuth=FirebaseAuth.getInstance()
        snapsListView=findViewById(R.id.snapsListView)
        emails= ArrayList()
        snaps= ArrayList()
        val adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,emails)
       snapsListView.adapter=adapter

        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.currentUser?.uid.toString()).child("snaps").addChildEventListener(object:ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                emails.add(p0.child("from").value as String)
                snaps.add(p0)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                var index=0
                for (snap:DataSnapshot in snaps){
                    if(snap.key==p0.key){
                        snaps.removeAt(index)
                        emails.removeAt(index)
                    }
                    index++
                }
                adapter.notifyDataSetChanged()
            }


        })

        snapsListView.onItemClickListener=AdapterView.OnItemClickListener{adapterView,view,i,l->

            val snapshot=snaps.get(i)
            var intent=Intent(this,viewSnap::class.java)

            intent.putExtra("imageName",snapshot.child("imageName").value as String)
            intent.putExtra("imageURL",snapshot.child("imageURL").value as String)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("snapKey",snapshot.key)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.snaps_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.snap){
              startActivity(Intent(this,createSnaps::class.java))
        }else if(item.itemId==R.id.logout){
            firebaseAuth.signOut()
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        firebaseAuth.signOut()
    }
}
