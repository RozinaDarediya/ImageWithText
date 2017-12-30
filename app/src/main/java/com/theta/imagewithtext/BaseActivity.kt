package com.theta.imagewithtext

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by ashish on 30/12/17.
 */
open class BaseActivity : AppCompatActivity() {

    private var REQUEST: Int = 0
    var REQUEST_READ_EXTERNAL_PERMISSION = 11
    var REQUEST_CAMERA_PERMISSION = 10

    var positiveClick: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialogInterface, i ->
        Log.e("msg", "positiveClick")
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        if (REQUEST == REQUEST_READ_EXTERNAL_PERMISSION) {
            startActivityForResult(intent, REQUEST_READ_EXTERNAL_PERMISSION)
        }
        if (REQUEST == REQUEST_CAMERA_PERMISSION) {
            startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)


    }

    fun checkPermisiion() {
        if (!checkReadPermission()) {
            REQUEST = REQUEST_READ_EXTERNAL_PERMISSION
            getReadExternalPermission()
        } else if (!checkCameraPermission()) {
            REQUEST = REQUEST_CAMERA_PERMISSION
            getCameraPermission()
        }
    }

    fun checkReadPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    fun checkCameraPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            false
        } else true
    }

    fun getCameraPermission() {
        REQUEST = REQUEST_CAMERA_PERMISSION
        if (ContextCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    fun getReadExternalPermission() {
        REQUEST = REQUEST_READ_EXTERNAL_PERMISSION
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), REQUEST_READ_EXTERNAL_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_EXTERNAL_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                Log.e("msg", "storage permisson granted")
                checkPermisiion()
            } else {
                Log.e("msg", "set to never ask again")
                AppDialog.showAppSettingDialog(this,
                        getString(R.string.txt_read_permission_title), getString(R.string.txt_read_permission),
                        positiveClick);
            }
        }
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED) {
                Log.e("msg", "camera permisson granted")
                checkPermisiion()
            } else {
                Log.e("msg", "set to never ask again")
                AppDialog.showAppSettingDialog(this,
                        getString(R.string.txt_camera_permission_title), getString(R.string.txt_camera_permission),
                        positiveClick)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("msg", "on activoty result base activity")
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED) {
                Log.e("msg", "permisson granted")
            } else {
                Log.e("msg", "set to never ask again")
                AppDialog.showAppSettingDialog(this,
                        getString(R.string.txt_camera_permission_title), getString(R.string.txt_camera_permission),
                        positiveClick)
            }
        } else if (requestCode == REQUEST_READ_EXTERNAL_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
                Log.e("msg", "permisson granted")
            } else {
                Log.e("msg", "set to never ask again")
                AppDialog.showAppSettingDialog(this,
                        getString(R.string.txt_read_permission_title), getString(R.string.txt_camera_permission),
                        positiveClick)
            }
        }
    }
}