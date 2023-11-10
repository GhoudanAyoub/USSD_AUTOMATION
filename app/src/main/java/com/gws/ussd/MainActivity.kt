package com.gws.ussd

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.gws.networking.providers.CurrentServerProvider
import com.gws.networking.providers.CurrentUserProvider
import com.gws.ussd.databinding.ActivityMainBinding
import com.gws.ussd.ui.splash.SplashActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var currentUserProvider: CurrentUserProvider
    @Inject
    lateinit var currentServerProvider: CurrentServerProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val numberOfSimCards = getNumberOfSimCards(this)
        binding.simNumber.text = StringBuilder()
            .append(numberOfSimCards.toString())
            .append(if (numberOfSimCards == 1) " carte SIM" else " cartes SIM")

        setupView()
        setupMenu()

    }

    fun getNumberOfSimCards(context: Context): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            val subscriptionManager =
                this.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return 0
            }
            val activeSubscriptions: List<SubscriptionInfo> =
                subscriptionManager.activeSubscriptionInfoList

            return activeSubscriptions.size
        } else {
            val telephonyManager =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return if (telephonyManager.simState == TelephonyManager.SIM_STATE_READY) 1 else 0
        }
    }

    private fun setupView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            this.setDisplayShowTitleEnabled(true)
            this.setDisplayShowHomeEnabled(true)
            this.setDisplayHomeAsUpEnabled(true)
            this.setHomeButtonEnabled(true)
        }
    }

    private fun setupMenu() {

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navigationView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            navController.graph, drawerLayout
        )
        navController?.let {
            setupActionBarWithNavController(it, appBarConfiguration)
            navView.setupWithNavController(it)
        }
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    binding.drawerLayout.closeDrawers()
                }
                R.id.navigation_server -> {
                    navController.navigate(R.id.navigation_server)
                    binding.drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        currentUserProvider.clearUser()
                    }
                    startActivity(Intent(this@MainActivity, SplashActivity::class.java))
                    this@MainActivity.finish()
                    binding.drawerLayout.closeDrawers()
                }
            }
            true
        }

    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount > 1) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }

    fun hideSoftKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) inputMethodManager.hideSoftInputFromWindow(
            currentFocus?.windowToken, 0
        )
    }

    fun showLoader() {
        if (binding.contentId.loaderOverlay?.visibility == View.GONE) {
            binding.contentId.loaderOverlay?.visibility = View.VISIBLE
        }
    }

    fun hideLoader() {
        if (binding.contentId.loaderOverlay?.visibility == View.VISIBLE) {
            binding.contentId.loaderOverlay?.visibility = View.GONE
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    public fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment_main, fragment)
            .addToBackStack(fragment::class.simpleName).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }
}
