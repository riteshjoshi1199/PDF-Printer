package com.example.pdfprinter

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.pdfprinter.databinding.ActivityMainBinding
import com.example.pdfprinter.ui.fragments.MainActivityViewModel
import com.google.android.material.navigation.NavigationView

class MainActivity: AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        /*   binding.appBarMain.fab?.setOnClickListener { view ->
               Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                   .setAction("Action", null)
                   .setAnchorView(R.id.fab).show()
           }*/

        val navHostFragment = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController

        binding.navView?.let {
            appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_pdf, R.id.nav_settings
            ),
                binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }

        binding.appBarMain.contentMain.bottomNavView?.let {
            appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_pdf, R.id.nav_settings))
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        // Using findViewById because NavigationView exists in different layout files
        // between w600dp and w1240dp
        val navView: NavigationView? = findViewById(R.id.nav_view)
        if (navView == null) {
            // The navigation drawer already has the items including the items in the overflow menu
            // We only inflate the overflow menu if the navigation drawer isn't visible
            menuInflater.inflate(R.menu.overflow, menu)
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_reload -> {
                try {
                    mainActivityViewModel.getDataForThiSession()
                } catch (error: Exception) {
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
            }

            R.id.nav_pdf -> {
                Toast.makeText(this, "Implement Later", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}