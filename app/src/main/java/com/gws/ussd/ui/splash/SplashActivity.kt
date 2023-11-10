package com.gws.ussd.ui.splash

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gws.ussd.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Permission denied; request the permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            showPermissionRationaleDialog(this)
        }

    }

    private fun showPermissionRationaleDialog(context: Context) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle("Autorisation requise")
        dialogBuilder.setMessage("Pour vous offrir le meilleur service, nous avons besoin d'accéder à votre numéro de téléphone. Veuillez accorder la permission.")
        dialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->

            // User has previously denied the permission without explanation, provide an option to open settings

            ActivityCompat.requestPermissions(
                this, // Cast the context to an Activity
                arrayOf(
                    Manifest.permission.READ_PHONE_NUMBERS,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE
                ),
                123
            )
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            // Handle the case where the user clicks "Cancel" (optional)
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
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
