package com.example.snapzhits

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_create_snaps.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.lang.ref.PhantomReference
import java.util.*
import java.util.jar.Manifest

class createSnaps : AppCompatActivity() {

    lateinit var snapImage:ImageView
    lateinit var message: EditText
    lateinit var storageReference: StorageReference
    val imageName=UUID.randomUUID().toString()+".jpg"
    lateinit var downloadUrl:Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snaps)

        snapImage=findViewById(R.id.snapImage)
        message=findViewById(R.id.editText)
    }


    fun chooseImage(view:View){
        val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage=data!!.data

        if(requestCode==1&&resultCode==Activity.RESULT_OK&&data!=null){
            try {
                val bitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,selectedImage)
               snapImage.setImageBitmap(bitmap)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun nextClick(view: View){
// Get the data from an ImageView as bytes

        snapImage.isDrawingCacheEnabled = true
        snapImage.buildDrawingCache()
        val bitmap = (snapImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        storageReference=FirebaseStorage.getInstance().getReference().child("images").child(imageName)

        var uploadTask = storageReference.putBytes(data)



        uploadTask.addOnFailureListener(OnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(this,"Image couldn't be uploaded",Toast.LENGTH_SHORT).show()

        }).addOnSuccessListener (OnSuccessListener<UploadTask.TaskSnapshot>{ taskSnapshot ->
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
// Task<Uri> downloadUri = taskSnapshot!.getStorage().getDownloadUrl();

                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result

                    Log.i("download Uri",downloadUrl.toString())
                    Toast.makeText(this,"Successfully uploaded",Toast.LENGTH_SHORT).show()

                    val intent =Intent(this,chooseUser::class.java)
                    intent.putExtra("imageURL",downloadUrl.toString())
                    intent.putExtra("imageName",imageName)
                    intent.putExtra("message",message.text.toString())
                    startActivity(intent)
                } else {
                    // Handle failures
                    // ...
                }
            }
        })
    }
}
