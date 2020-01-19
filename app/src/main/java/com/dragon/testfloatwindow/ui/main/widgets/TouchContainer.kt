package com.dragon.testfloatwindow.ui.main.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * @author chenjiulong
 */

class TouchContainer : FrameLayout {

    var interupted = false


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
                    Log.d(
                        "dragon_debug",
                        "onInterceptTouchEvent action ${ev.actionMasked} actionIndex ${ev.actionIndex}  getPointerId ${ev.getPointerId(
                            ev.actionIndex
                        )}"
                    )
                    interupted = true
                }
                MotionEvent.ACTION_UP -> {
                    Log.d(
                        "dragon_debug",
                        "onInterceptTouchEvent action ${ev.actionMasked} actionIndex ${ev.actionIndex}  getPointerId ${ev.getPointerId(
                            ev.actionIndex
                        )}"
                    )
                }
                else -> {

                }
            }
        }
        if (interupted) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d(
                        "dragon_debug",
                        "onTouchEvent action ${it.actionMasked} actionIndex ${it.actionIndex}  getPointerId ${it.getPointerId(
                            it.actionIndex
                        )}"
                    )
                    interupted = true
                }
                MotionEvent.ACTION_POINTER_DOWN ->{
                    Log.d(
                        "dragon_debug",
                        "onTouchEvent action ${it.actionMasked} actionIndex ${it.actionIndex}  getPointerId ${it.getPointerId(
                            it.actionIndex
                        )}"
                    )

                }
                MotionEvent.ACTION_POINTER_UP->{
                    Log.d(
                        "dragon_debug",
                        "onTouchEvent action ${it.actionMasked} actionIndex ${it.actionIndex}  getPointerId ${it.getPointerId(
                            it.actionIndex
                        )}"
                    )

                }
                MotionEvent.ACTION_UP -> {
                    Log.d(
                        "dragon_debug",
                        "onTouchEvent action ${it.actionMasked} actionIndex ${it.actionIndex}  getPointerId ${it.getPointerId(
                            it.actionIndex
                        )}"
                    )
                }
                else -> {

                }
            }
        }
        if (interupted) {
            return true
        }
        return super.onInterceptTouchEvent(event)
    }
}
