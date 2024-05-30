package com.a213310009ayubbudisantoso.transmart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleNotificationIntent(intent)
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.fragmentContainerView)
        when (navController.currentDestination?.id) {
            R.id.loginFragment -> {
                if (doubleBackToExitPressedOnce) {
                    finish()
                } else {
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
            }
            R.id.bebasExpiredFragment -> {
                navController.navigate(R.id.action_bebasExpiredFragment_to_homeFragment)
            }
            R.id.tarikBarangFragment -> {
                navController.navigate(R.id.action_tarikBarangFragment_to_bebasExpiredFragment)
            }
            R.id.homeFragment -> {
                moveTaskToBack(true)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.action == "OPEN_TARIK_BARANG_FRAGMENT") {
            // Navigate to tarikBarangFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.findNavController()
            navController.navigate(R.id.tarikBarangFragment)
        }
    }}
