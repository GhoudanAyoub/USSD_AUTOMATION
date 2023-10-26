package com.gws.ussd

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.gws.networking.providers.CurrentServerProvider
import com.gws.networking.providers.CurrentUserProvider
import dagger.hilt.android.AndroidEntryPoint
import com.gws.ussd.databinding.ActivityMainBinding
import com.gws.ussd.ui.home.HomeViewModel
import com.gws.ussd.ui.login.LoginFragment
import com.gws.ussd.ui.splash.SplashActivity
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

        setupView()
        setupMenu()
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
                R.id.logout -> {
                    CoroutineScope(Dispatchers.Default).launch {
                        currentUserProvider.clearUser()
                        currentServerProvider.clearServer()
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
        if (binding.loaderOverlay?.visibility == View.GONE) {
            binding.loaderOverlay?.visibility = View.VISIBLE
        }
    }

    fun hideLoader() {
        if (binding.loaderOverlay?.visibility == View.VISIBLE) {
            binding.loaderOverlay?.visibility = View.GONE
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

}
