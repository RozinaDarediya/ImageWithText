package com.theta.imagewithtext

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.theta.imagewithtext.Global.Companion.createImageFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : BaseActivity(), MyDialogFragment.EditDialogListener {


    private val REQUEST_GALLERY_PHOTO = 0
    private val REQUEST_CAMERA_PHOTO = 1
    var REQUEST_READ_EXTERNAL_PERMISSIONs = 111
    var REQUEST_CAMERA_PERMISSIONs = 110

    private var bitmapImage: Bitmap? = null
    private var uri: Uri? = null
    private var imgText: String = ""

    private lateinit var f: File
    private var mCurrentPhotoPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        checkPermisiion()
    }

    private fun init() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menuCamera -> {
                val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, REQUEST_CAMERA_PHOTO)
            }
            R.id.menuGallery -> {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent, REQUEST_GALLERY_PHOTO)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("msg", "on activoty result main activty")
        if (requestCode == REQUEST_GALLERY_PHOTO) {
            if (data != null) {
                galleryIntent(data)
            }
        }
        if (requestCode == REQUEST_CAMERA_PHOTO) {
            if (data != null) {
                cameraIntent(data)
            }
        }
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.e("msg", "permisson granted")
                checkPermisiion();
            } else {
                Log.e("msg", "set to never ask again")
                AppDialog.showAppSettingDialog(this,
                        getString(R.string.txt_camera_permission_title), getString(R.string.txt_camera_permission),
                        positiveClick)
            }
        }
        if (requestCode == REQUEST_READ_EXTERNAL_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.e("msg", "permisson granted")
                checkPermisiion()
            } else {
                Log.e("msg", "set to never ask again")
                AppDialog.showAppSettingDialog(this,
                        getString(R.string.txt_read_permission_title), getString(R.string.txt_read_permission),
                        positiveClick)
            }
        }

    }

    private fun cameraIntent(data: Intent) {
        try {
            val photo = data.extras.get("data") as Bitmap
            ivImage.setImageBitmap(photo)
            //get text to write on image
            var myDialogFragment = MyDialogFragment.newInstance()
            myDialogFragment.show(fragmentManager, "dialog")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("error", e.toString())
        }
    }

    private fun galleryIntent(data: Intent?) {
        uri = data!!.data
        try {
            bitmapImage = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            // Log.d(TAG, String.valueOf(bitmap));
            ivImage.setImageBitmap(bitmapImage)

            //get text to write on image
            var myDialogFragment = MyDialogFragment.newInstance()
            myDialogFragment.show(fragmentManager, "dialog")

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("error", e.toString())
        }
    }

    override fun updateResult(inputText: String) {
        imgText = inputText
        Log.e("text", imgText)
        textView.setText(imgText)
        textView.visibility = View.VISIBLE
        val bm = Global.getBitmapFromView(layout)
        SaveImage(bm)
    }

    private fun SaveImage(bitmapImage: Bitmap?) {
        var file: File? = null
        try {
            file = setUpPhotoFile()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("error", e.toString())
        }
        if (file!!.exists())
            file.delete()

        // writes the file
        try {
            val out = FileOutputStream(file)
            bitmapImage!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(this, "image saved in file", Toast.LENGTH_LONG).show();
            Log.e("file", "img saved")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // show the image in the device gallery
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val contentUri = Uri.fromFile(file) //out is your output file
            mediaScanIntent.data = contentUri
            this.sendBroadcast(mediaScanIntent)
        } else {
            sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())))
        }
    }

    private fun setUpPhotoFile(): File? {
        f = createImageFile(this)
        mCurrentPhotoPath = f.absolutePath
        return f
    }


}
