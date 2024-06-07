package com.a213310009ayubbudisantoso.transmart

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage

class MainActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleNotificationIntent(intent)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
//            val msg = getString(R.string.msg_token_fmt, token)
            if (token != null) {
                Log.d("ini adalah token", token)
            }
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

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
            R.id.detailItemNotifFragment -> {
                navController.navigate(R.id.action_detailItemFragment_to_tarikBarangFragment)
            }
            R.id.homeFragment -> {
                moveTaskToBack(true)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: ${remoteMessage.from}")
//
//        // Check if message contains a data payload.
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
//
//            // Check if data needs to be processed by long running job
//            if (needsToBeScheduled()) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
//        }
//
//        // Check if message contains a notification payload.
//        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it.body}")
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }


    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.action == "OPEN_TARIK_BARANG_FRAGMENT") {
            // Navigate to tarikBarangFragment
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.findNavController()
            navController.navigate(R.id.detailItemNotifFragment)
        }
    }

    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
//    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token)
//    }


}
