package com.dragon.testfloatwindow

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.dragon.testfloatwindow.ui.main.MainFragment
import com.dragon.testfloatwindow.ui.main.service.FloatWindowService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
        if (canShowFloat()) {
//            startService(Intent(this, FloatWindowService::class.java))
        } else {
            requestShowFloatPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (requestCode == 100) {
                if (Settings.canDrawOverlays(this)) {
                    startService(Intent(this, FloatWindowService::class.java))
                }
            }
        }
    }


    private fun canShowFloat(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) Settings.canDrawOverlays(this) else true
    }

    private fun requestShowFloatPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, 100)
        }
    }

}
