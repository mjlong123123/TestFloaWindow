package com.dragon.testfloatwindow.ui.main.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
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

class FloatWindowService : Service() {

    private val windowManager: WindowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val rootContainer: ViewGroup by lazy {
        val rootContainer = TouchContainer(this)
        rootContainer.setBackgroundColor(Color.BLACK)
        rootContainer.id = R.id.container
        rootContainer.listener = object : TouchContainer.Listener{
            override fun move(dx: Int, dy: Int) {
                updateWindowPosition(dx,dy)
            }
        }
        rootContainer
    }
    private val layoutParameter: WindowManager.LayoutParams by lazy {
        createWindowParams(400, 400)
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
                    getFragmentManager().beginTransaction().replace(R.id.container, BlankFragment())
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
        Log.d("dragon_debug", "onCreate ")
    }


    override fun onDestroy() {
        super.onDestroy()
        mFragments.dispatchDestroy()
        windowManager.removeView(rootContainer)
        Log.d("dragon_debug", "onDestroy ")
    }

    private fun getFragmentManager(): FragmentManager {
        return mFragments.supportFragmentManager
    }

    private fun updateWindowPosition(dx:Int,dy:Int){
        Log.d("dragon_update","$dx $dy")
        layoutParameter.x = layoutParameter.x + dx
        layoutParameter.y = layoutParameter.y + dy
        windowManager.updateViewLayout(rootContainer,layoutParameter)
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
                or WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)
        params.format = PixelFormat.TRANSLUCENT
        params.gravity = Gravity.LEFT or Gravity.TOP
        return params
    }
}
