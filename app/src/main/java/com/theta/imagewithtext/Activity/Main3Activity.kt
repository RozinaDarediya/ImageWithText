package com.theta.imagewithtext.Activity

import android.graphics.PointF
import android.opengl.Matrix
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.theta.imagewithtext.R


class Main3Activity : AppCompatActivity() {

    private var gestureDetector: GestureDetector? = null
    private var gestureListener: View.OnTouchListener? = null

    var mTwoFingersTapped: Boolean? = false
    // These matrices will be used to move and zoom image
     val matrix: Matrix =  Matrix();
    val savedMatrix:Matrix =  Matrix();
    // We can be in one of these 3 states
    companion object {
        final val NONE: Int = 0
        final val DRAG: Int = 0
        final val ZOOM: Int = 0
    }

    val start: PointF  = PointF()
    val mid: PointF  = PointF()
    var oldDist = 1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val detect: PrevGestureDetector = PrevGestureDetector()
        detect.setActivity(this)

        gestureDetector = GestureDetector(this, detect)
        //gestureListener = View.OnTouchListener()
    }

    class PrevGestureDetector : GestureDetector.SimpleOnGestureListener() {
        var mainActivity: Main3Activity? = null
        public fun setActivity( main3Activity: Main3Activity){
            mainActivity = main3Activity
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }
    }
}
