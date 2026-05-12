package com.project.composeproject.ui.screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.project.composeproject.ui.navigation.AppNavigation
import com.project.composeproject.ui.utils.applySystemBarsSetting
import com.project.composeproject.utils.LanguageUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : ComponentActivity() {

    companion object {
        fun newIntent(activity: AppCompatActivity): Intent {
            return Intent(activity, LauncherActivity::class.java)
        }
    }


    override fun attachBaseContext(newBase: Context) {
        val localizedContext = LanguageUtils.createLocalizedContext(newBase.applicationContext)
        super.attachBaseContext(localizedContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySystemBarsSetting()
        setContent { AppNavigation() }
    }
}