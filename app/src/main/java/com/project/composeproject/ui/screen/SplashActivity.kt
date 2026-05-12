package com.project.composeproject.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.project.composeproject.databinding.ActivitySplashBinding
import com.project.composeproject.ui.utils.applySystemBarsSetting
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        applySystemBarsSetting()
        setContentView(binding.root)

        startLauncherActivity()
    }


    private fun startLauncherActivity() {
        lifecycleScope.launch {
            delay(1_000)
            val intent = LauncherActivity.newIntent(this@SplashActivity)
            startActivity(intent)
            finishAffinity()
        }
    }
}