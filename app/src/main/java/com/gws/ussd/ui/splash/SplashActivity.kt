package com.gws.ussd.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.gws.common.utils.UssdNavigation
import com.gws.ussd.MainActivity
import com.gws.ussd.R
import com.gws.ussd.databinding.ActivityMainBinding
import com.gws.ussd.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun showLoader() {
        if (binding.loaderOverlay?.visibility == View.GONE) {
            binding.loaderOverlay?.visibility = View.VISIBLE
        }
    }

    fun hideLoader() {
        if (binding.loaderOverlay?.visibility == View.VISIBLE) {
            binding.loaderOverlay?.visibility = View.GONE
        }
    }
}