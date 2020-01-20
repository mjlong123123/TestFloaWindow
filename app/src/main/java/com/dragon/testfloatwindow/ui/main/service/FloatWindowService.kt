package com.dragon.testfloatwindow.ui.main.service

import android.animation.LayoutTransition
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.fragment.app.FragmentController
import androidx.fragment.app.FragmentHostCallback
import androidx.fragment.app.FragmentManager
import com.dragon.testfloatwindow.R
import com.dragon.testfloatwindow.ui.main.BlankFragment
import com.dragon.testfloatwindow.ui.main.widgets.TouchContainer
import kotlin.math.absoluteValue

class FloatWindowService : Service() {

    private val windowManager: WindowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val rootContainer: ViewGroup by lazy {
        val rootContainer = TouchContainer(this)
        rootContainer.setBackgroundColor(Color.BLACK)
        rootContainer.id = R.id.window_root_id
        rootContainer.layoutTransition = LayoutTransition()
        rootContainer.listener = object : TouchContainer.Listener {
            override fun move(dx: Int, dy: Int) {
                updateWindowPosition(dx, dy)
            }
        }
        rootContainer
    }
    private val layoutParameter: WindowManager.LayoutParams by lazy {
        val params = WindowManager.LayoutParams()
        params.width = 400
        params.height = 400
        params.type = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1 -> WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            else -> WindowManager.LayoutParams.TYPE_TOAST
        }
        params.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        params.format = PixelFormat.TRANSLUCENT
        params.gravity = Gravity.LEFT or Gravity.TOP
        params
    }

    private val displayRect: Rect by lazy {
        val rect = Rect()
        windowManager.defaultDisplay.getRectSize(rect)
        rect
    }


    private val mFragments =
        FragmentController.createController(object : FragmentHostCallback<Service>(
            this,
            Handler(Looper.getMainLooper()),
            0
        ) {
            override fun onGetHost(): Service? {
                return this@FloatWindowService
            }

            override fun onFindViewById(id: Int): View? {
                return rootContainer.findViewById(id)
            }

            override fun onGetLayoutInflater(): LayoutInflater {
                return super.onGetLayoutInflater().cloneInContext(this@FloatWindowService)
            }
        })

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                "show window" -> {
                    getFragmentManager().beginTransaction().replace(R.id.window_root_id, BlankFragment())
                        .commit()
                }
                else -> {

                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onCreate() {
        super.onCreate()
        windowManager.addView(rootContainer, layoutParameter)
        mFragments.attachHost(null)
        mFragments.dispatchResume()
    }


    override fun onDestroy() {
        super.onDestroy()
        mFragments.dispatchDestroy()
        windowManager.removeView(rootContainer)
    }

    private fun getFragmentManager(): FragmentManager {
        return mFragments.supportFragmentManager
    }

    fun updateWindowSize(width: Int, height: Int) {
        if (layoutParameter.width != width || layoutParameter.height != height) {
            layoutParameter.width = width
            layoutParameter.height = height
            checkRange()
            windowManager.updateViewLayout(rootContainer, layoutParameter)
        }
    }

    private fun updateWindowPosition(dx: Int, dy: Int) {
        if (dx.absoluteValue > 0 || dy.absoluteValue > 0) {
            Log.d("dragon_p", "x ${layoutParameter.x} y ${layoutParameter.y}")
            layoutParameter.x = layoutParameter.x + dx
            layoutParameter.y = layoutParameter.y + dy
            checkRange()
            windowManager.updateViewLayout(rootContainer, layoutParameter)
        }
    }

    private fun checkRange() {
        when {
            layoutParameter.x < 0 -> layoutParameter.x = 0
            layoutParameter.x + layoutParameter.width > displayRect.width() -> {
                layoutParameter.x = displayRect.width() - layoutParameter.width
            }
        }
        when {
            layoutParameter.y < 0 -> layoutParameter.y = 0
            layoutParameter.y + layoutParameter.height > displayRect.height() -> {
                layoutParameter.y = displayRect.height() - layoutParameter.height
            }
        }
    }

    private fun createWindowParams(width: Int, height: Int): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        params.width = width
        params.height = height
        params.type = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1 -> WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            else -> WindowManager.LayoutParams.TYPE_TOAST
        }
        params.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                or WindowManager.LayoutParams.FLAG_FULLSCREEN)
        params.format = PixelFormat.TRANSLUCENT
        params.gravity = Gravity.LEFT or Gravity.TOP
        return params
    }
}
