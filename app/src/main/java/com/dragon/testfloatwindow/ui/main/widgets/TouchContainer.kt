package com.dragon.testfloatwindow.ui.main.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import kotlin.math.absoluteValue

/**
 * @author chenjiulong
 */

class TouchContainer : FrameLayout {

    private var interrupted = false
    private var lastX = 0f
    private var lastY = 0f
    private var activePointId = 0
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    var listener: Listener? = null


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let {
            when (it.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = it.rawX
                    lastY = it.rawY
                    activePointId = it.getPointerId(it.actionIndex)
                    interrupted = false
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentPointId = it.getPointerId(it.actionIndex)
                    if (currentPointId == activePointId) {
                        val currentX = it.rawX
                        val currentY = it.rawY
                        if ((currentX - lastX).absoluteValue > touchSlop || (currentY - lastY).absoluteValue > touchSlop) {
                            lastX = currentX
                            lastY = currentY
                            requestDisallowInterceptTouchEvent(true)
                            interrupted = true
                        }
                    }
                }
                else -> {

                }
            }
        }
        if (interrupted) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.actionMasked) {
                MotionEvent.ACTION_MOVE -> {

                    val currentPointId = it.getPointerId(it.actionIndex)
                    if (currentPointId == activePointId) {
                        val currentX = it.rawX
                        val currentY = it.rawY

                        Log.d("dragon_move", " $currentX $currentY")
                        listener?.move((currentX - lastX).toInt(), (currentY - lastY).toInt())
                        lastX = currentX
                        lastY = currentY
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                }
                else -> {

                }
            }
        }
        return true
    }


    interface Listener {
        fun move(dx: Int, dy: Int)
    }
}
