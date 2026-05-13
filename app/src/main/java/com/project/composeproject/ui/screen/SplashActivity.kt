package com.project.composeproject.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.project.composeproject.databinding.ActivitySplashBinding
import com.project.composeproject.ui.utils.applySystemBarsSetting
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private var player: ExoPlayer? = null

    private val videoMediaItem = MediaItem.fromUri("asset:///video_splash.mp4")


    override fun onStart() {
        super.onStart()
        player?.play()
    }

    override fun onStop() {
        player?.pause()
        super.onStop()
    }

    override fun onDestroy() {
        binding.playerView.player = null
        player?.release()
        player = null
        super.onDestroy()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        applySystemBarsSetting()
        setContentView(binding.root)

        setupVideoPlayer()
        startLauncherActivity()
    }


    private fun setupVideoPlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            exoPlayer.setMediaItem(videoMediaItem)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            exoPlayer.playWhenReady = true
            exoPlayer.prepare()
        }
    }


    private fun startLauncherActivity() {
        lifecycleScope.launch {
            delay(5_000)
            val intent = LauncherActivity.newIntent(this@SplashActivity)
            startActivity(intent)
            finishAffinity()
        }
    }
}
