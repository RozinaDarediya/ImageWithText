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
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.theta.imagewithtext.Global.Companion.createImageFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException






class MainActivity : BaseActivity(), MyDialogFragment.EditDialogListener, View.OnTouchListener, View.OnClickListener {



    private val REQUEST_GALLERY_PHOTO = 0
    private val REQUEST_CAMERA_PHOTO = 1
    var REQUEST_READ_EXTERNAL_PERMISSIONs = 111
    var REQUEST_CAMERA_PERMISSIONs = 110

    private var bitmapImage: Bitmap? = null
    private var uri: Uri? = null
    private var imgText: String = ""

    private lateinit var f: File
    private var mCurrentPhotoPath: String? = null

    var dX: Float = 0.toFloat()
    var dY: Float = 0.toFloat()

    //--------------------- for image move-----------------
    private var xCoOrdinate: Float = 0.toFloat()
    private var yCoOrdinate: Float = 0.toFloat()

    //--------------------- for textview zoom in/out -------
    internal val STEP = 200f
    internal var mRatio = 1.0f
    internal var mBaseDist: Int = 0
    internal var mBaseRatio: Float = 0.toFloat()
    internal var fontsize = 13f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        checkPermisiion()
        /*btnsave.setOnClickListener(View.OnClickListener {
            Log.e("msg", "btn click")
            val bm = Global.getBitmapFromView(layout)
            SaveImage(bm)
        })*/
        btnsave.setOnClickListener(this)
    }

    private fun init() {
        tvMarquee.isSelected = true
        textView.setTextSize(mRatio + 40)
      //  textView.setZoomLimit(6.0f)
      //  ivImage.setOnTouchListener(this)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount == 2) {
            // zoom in/out of textview
            val action = event.action
            val pureaction = action and MotionEvent.ACTION_MASK
            if (pureaction == MotionEvent.ACTION_POINTER_DOWN) {
                mBaseDist = getDistance(event)
                mBaseRatio = mRatio
            } else {
                val delta = (getDistance(event) - mBaseDist) / STEP
                val multi = Math.pow(2.0, delta.toDouble()).toFloat()
                mRatio = Math.min(1024.0f, Math.max(0.1f, mBaseRatio * multi))
                textView.setTextSize(mRatio + 30)
            }
        }else{
            // move textview
            when (event!!.getAction()) {
                MotionEvent.ACTION_DOWN -> {
                    dX = textView!!.getX() - event.getRawX()
                    dY = textView.getY() - event.getRawY()
                }
                MotionEvent.ACTION_MOVE ->
                    textView!!.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start()
                else -> return false
            }
        }
        return true
    }

    internal fun getDistance(event: MotionEvent): Int {
        val dx = (event.getX(0) - event.getX(1)).toInt()
        val dy = (event.getY(0) - event.getY(1)).toInt()
        return Math.sqrt((dx * dx + dy * dy).toDouble()).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true;
    }

    override fun onClick(view: View?) {
        if (view!!.id == btnsave.id){
            Log.e("msg", "btn click")
            val bm = Global.getBitmapFromView(layout)
            textView.visibility = View.INVISIBLE
            ivImage.setImageBitmap(bm)
            SaveImage(bm)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        btnsave.visibility = View.GONE
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
        textView.setOnTouchListener(this)
        var snackbar: Snackbar = Snackbar.make(textView, "You can move the position of textview.", Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction("OK", View.OnClickListener {
            snackbar.dismiss()
            btnsave.visibility = View.VISIBLE
        })
        snackbar.show()
        //  val bm = Global.getBitmapFromView(layout)
        // SaveImage(bm)
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

    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        /*
        // move textview
        if (view!!.id == textView.id){
            when (event!!.getAction()) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view!!.getX() - event.getRawX()
                    dY = view.getY() - event.getRawY()
                }
                MotionEvent.ACTION_MOVE ->
                    view!!.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start()
                else -> return false
            }

        }*/

        /*
        // move img
        if (view!!.id == ivImage.id){
            when (event!!.getActionMasked()) {
                MotionEvent.ACTION_DOWN -> {
                    xCoOrdinate = view.x - event.getRawX()
                    yCoOrdinate = view.y - event.getRawY()
                }
                MotionEvent.ACTION_MOVE ->
                    view.animate()
                            .x(event.getRawX() + xCoOrdinate)
                            .y(event.getRawY() + yCoOrdinate)
                            .setDuration(0)
                            .start()
                else -> return false
            }
            return true
        }*/
       return false
    }



}

