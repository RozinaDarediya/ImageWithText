package com.theta.imagewithtext.ZoomTextView

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.theta.imagewithtext.R
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.content_main2.*




class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val tv_sticker1 = StickerTextView(this)
        tv_sticker1.text = "nkDroid"
        canvasView.addView(tv_sticker1)


// add a stickerImage to canvas
        val iv_sticker = StickerImageView(this)
        iv_sticker.setImageDrawable(resources.getDrawable(R.drawable.ic_c3))
        canvasView.addView(iv_sticker)

        
    }

}
