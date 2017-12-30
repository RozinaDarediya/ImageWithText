package com.theta.imagewithtext

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog

/**
 * Created by ashish on 30/12/17.
 */
class AppDialog {

    companion object {
        fun showAppSettingDialog(context: Context, title: String, msg: String,
                                 positiveClick: DialogInterface.OnClickListener?) {
            var alertDialog: AlertDialog? = null
            val alertDialogBuilder = AlertDialog.Builder(context)
            // set title
            alertDialogBuilder.setTitle(title)
            // set dialog message
            alertDialogBuilder
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton(context.getText(R.string.txt_countinue), positiveClick)

            // create alert dialog
            alertDialog = alertDialogBuilder.create()
            alertDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
            // show it
            alertDialog.show()
        }
    }
}