package com.dragon.testfloatwindow.ui.main

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment

import com.dragon.testfloatwindow.R
import kotlinx.android.synthetic.main.player_fragment.*
import kotlinx.android.synthetic.main.player_fragment.view.*
import kotlinx.coroutines.*
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class PlayerFragment : Fragment(), CoroutineScope {

    companion object {
        fun newInstance() = PlayerFragment()
    }

    private lateinit var viewModel: PlayerViewModel

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val player = MediaPlayer()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.player_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch {
            val initSurfaceTask = launch { setSurface(player) }
            initSurfaceTask.join()
            player.setDataSource("mnt/sdcard/test.mp4")
            player.prepare()
            player.start()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProviders.of(this).get(PlayerViewModel::class.java)
    }

    suspend fun setSurface(mediaPlayer: MediaPlayer) {
        val completableDeferred = CompletableDeferred<SurfaceTexture>()
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                if (surface != null) {
                    completableDeferred.complete(surface)
                } else {
                    completableDeferred.completeExceptionally(RuntimeException("surface error"))
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                if (surface != null) {
                    completableDeferred.complete(surface)
                } else {
                    completableDeferred.completeExceptionally(RuntimeException("surface error"))
                }
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
                if (surface != null) {
                    completableDeferred.complete(surface)
                } else {
                    completableDeferred.completeExceptionally(RuntimeException("surface error"))
                }
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                completableDeferred.completeExceptionally(RuntimeException("surface error"))
                return true
            }
        }
        if (textureView.isAvailable) {
            Log.e("dragon_thread", "setSurface directly ${Thread.currentThread()}")
            Log.e("dragon_thread", "setSurface directly before ${Thread.currentThread()}")
            delay(3000)
            Log.e("dragon_thread", "setSurface directly after ${Thread.currentThread()}")
            mediaPlayer.setSurface(Surface(textureView.surfaceTexture))
        } else {
            Log.e("dragon_thread", "setSurface before wait ${Thread.currentThread()}")
            val surfaceTexture: SurfaceTexture = completableDeferred.await()
            Log.e("dragon_thread", "setSurface after wait ${Thread.currentThread()}")
            mediaPlayer.setSurface(Surface(surfaceTexture))
        }
    }
}
