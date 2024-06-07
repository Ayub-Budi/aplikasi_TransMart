package com.a213310009ayubbudisantoso.transmart

import DashboardExpiredModel
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.a213310009ayubbudisantoso.transmart.api.model.DashboardResponse
import com.a213310009ayubbudisantoso.transmart.api.model.ItemNotifyModel
import com.a213310009ayubbudisantoso.transmart.api.model.StoreNotifyModel
import com.a213310009ayubbudisantoso.transmart.api.service.NotifyService
import com.a213310009ayubbudisantoso.transmart.api.services.DasboardEspiredService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var nameUser: String? = null
    private var storeUser: String? = null
    private var deptName: String? = null
    private var nik: String? = null
    private var usp_dept: String? = null


    private val CHANNEL_ID = "example_channel"
    private val notificationId = 101
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bebasExpired = view.findViewById<CardView>(R.id.expired)
        val listBarang = view.findViewById<CardView>(R.id.listBarang)

        // Perbaiki
        val nameText = view.findViewById<TextView>(R.id.name)
        val storeText = view.findViewById<TextView>(R.id.storeName)

        val buttonShowBottomSheet = view.findViewById<ImageView>(R.id.buttonShowBottomSheet)
        buttonShowBottomSheet.setOnClickListener {
            val bottomSheet = ProfileBottomSheetFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        bebasExpired.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_bebasExpiredFragment)
        }

        listBarang.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }


        displaySavedResponseUser()
        nameText.text = this.nameUser
        storeText.text = this.storeUser


        createNotificationChannel()
        fetchDataFromAPIDashboardModel()
        fetchDataFromAPIDasboardList()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
        } else {
            fetchDataFromAPINotify()
        }
    }

    private fun displaySavedResponseUser() {
        val sharedPreferences = requireContext().getSharedPreferences("response_data", Context.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("response_json", "")
        Log.d("ResponseJson", "$responseJson")

        if (!responseJson.isNullOrEmpty()) {
            try {
                val jsonObject = JSONObject(responseJson)

                // Extracting values from the JSON object
                val apiData = jsonObject.getJSONObject("apiData")
                val user = apiData.getJSONObject("user")
                val dbDataArray = jsonObject.getJSONArray("dbData")
                val dbData = if (dbDataArray.length() > 0) dbDataArray.getJSONObject(0) else null

                nameUser = user.optString("name", "Unknown User")
                nik = user.optString("nik", "Unknown User")
                usp_dept = dbData?.optString("usp_dept", "Unknown User")
                deptName = dbData?.optString("dept_name", "Unknown User")
                storeUser = dbData?.optString("store_name", "Unknown Store") ?: "Unknown Store"

                val uspUser = nik.toString()

                checkSession(uspUser)

                Log.d("ResponseJson", "Name: $nameUser, Store: $storeUser, usp_dept: $usp_dept, storeUser: $deptName")
            } catch (e: Exception) {
                Log.e("ResponseJson", "Error parsing response string", e)
            }
        } else {
            Log.d("ResponseJson", "No response data found")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Example Channel"
            val descriptionText = "This is an example channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun fetchDataFromAPIDashboardModel() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(DasboardEspiredService::class.java)

        val uspUser = nik.toString()
        val dpt = usp_dept.toString()

        apiService.getDashboardData(uspUser,dpt).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful) {
                    val dashboardResponse = response.body()
                    saveDataToSharedPreferences(requireContext(), dashboardResponse)
                    val dataListed = dashboardResponse?.dataListed
                    val dataWithdrawn = dashboardResponse?.dataWithdrawn

                    // Lakukan sesuatu dengan data yang diterima
                    Log.d("DashboardData", "Item listed today: ${dataListed?.itemListedToday}")
                    Log.d("DashboardData", "Total items listed: ${dataListed?.totalItemsListed}")
                    Log.d("DashboardData", "Item withdrawn today: ${dataWithdrawn?.itemWithdrawnToday}")
                    Log.d("DashboardData", "Total items withdrawn: ${dataWithdrawn?.totalItemsWithdrawn}")
                } else {
                    Log.e("DasboardEspired", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                // Tangani kesalahan koneksi atau respons gagal
                Log.e("DasboardEspired", "Error: ${t.message}")
            }
        })
    }

    private fun fetchDataFromAPIDasboardList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://backend.transmart.co.id/apiMobile/dashboard-expiringSoon?usp_user=$nik&usp_dept=$usp_dept"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e("HomeFragment", "Failed to retrieve data: ${response.code}")
                        return@use
                    }

                    val responseBody = response.body?.string()
                    responseBody?.let {
                        saveDataToSharedPreferences(requireContext(), it)
                        parseAndLogApiResponse(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", e.message ?: "Unknown error")
            }
        }
    }

    private fun fetchDataFromAPINotify() {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
//        val uspUser = "xxxxxxxxxxx" // replace with the actual user identifier
//        val requestUrl = "https://backend.transmart.co.id/apiMobile/notify-expired?usp_user=$uspUser"
//        Log.d("notifyResponse", "Request URL: $requestUrl")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(NotifyService::class.java)

        val uspUser = nik.toString() // replace with the actual user identifier
//        val requestUrl = "https://backend.transmart.co.id/apiMobile/notify-expired?usp_user=$uspUser"
//        Log.d("notifyResponse", "Request URL: $requestUrl")

        apiService.getExpiredNotifications(uspUser).enqueue(object : Callback<List<StoreNotifyModel>> {
            override fun onResponse(call: Call<List<StoreNotifyModel>>, response: Response<List<StoreNotifyModel>>) {
                if (response.isSuccessful) {
                    val notifyResponse = response.body()
                    Log.d("notifyResponse", "Response: $notifyResponse")
                    notifyResponse?.let { stores ->
                        stores.forEach { store ->
                            store.items.forEach { item ->
                                Log.d("notifyResponse ini", "${item.wording} ")
//                                sendNotification(item.wording, item.hashCode())
                                sendNotification(item, item.hashCode())
                            }
                        }
                    }
                } else {
                    Log.e("NotifyExpired", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<StoreNotifyModel>>, t: Throwable) {
                Log.e("NotifyExpired", "Error: ${t.message}")
            }
        })
    }
    private fun parseAndLogApiResponse(responseBody: String) {
        val gson = Gson()
        val apiResponse = gson.fromJson(responseBody, DashboardExpiredModel::class.java)
        Log.d("HomeFragment", "Total item nearing expiration: ${apiResponse.totalItemNearingExpiration}")
        Log.d("HomeFragment", "Nearest expiration date: ${apiResponse.nearestExpirationDate}")
        apiResponse.closestItems?.forEach {
            Log.d("HomeFragment", "Item Name: ${it.ieItemName}, Remaining Days: ${it.remainingDays}")
            //send notif
//            sendNotification("${it.ieItemName} expired dalam ${it.remainingDays} hari lagi", it.hashCode())
        }
    }

//    private fun sendNotification(message: String, notificationId: Int) {
//        val intent = Intent(requireContext(), MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }
//        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//        val vibrationPattern = longArrayOf(0, 500, 1000)
//
//        // Custom layout for the notification
//        val notificationLayout = RemoteViews(requireContext().packageName, R.layout.notification_popup)
//        notificationLayout.setTextViewText(R.id.notification_title, "Item Expiring Soon")
//        notificationLayout.setTextViewText(R.id.notification_message, message)
//
//        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
//            .setSmallIcon(R.drawable.logo_trans_tools)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//            .setCustomContentView(notificationLayout)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .setVibrate(vibrationPattern)
//            // Set the visibility to public to show the notification on lock screen
//            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//
//        with(NotificationManagerCompat.from(requireContext())) {
//            if (ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return
//            }
//            notify(notificationId, builder.build())
//        }
//    }


    private fun sendNotification(message: ItemNotifyModel, notificationId: Int) {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            Log.d("Ini Notif", "$message")
//            saveDataToSharedPreferences(requireContext(), "$message")
            val item = message.toString()
            saveDataToSharedPreferencesNotif(requireContext(), item)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("EXTRA_NOTIFICATION_MESSAGE", message.wording)
            action = "OPEN_TARIK_BARANG_FRAGMENT" // Menambahkan tindakan khusus untuk membuka fragment
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val vibrationPattern = longArrayOf(0, 500, 1000)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_trans_tools)
            .setContentTitle("Item Expiring Soon")
            .setContentText(message.wording)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(vibrationPattern)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        with(NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationId, builder.build())
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                fetchDataFromAPIDashboardModel()
                fetchDataFromAPIDasboardList()
            } else {
                // Handle the case where the user denies the permission
            }
        }
    }

    private fun saveDataToSharedPreferences(context: Context, itemList: DashboardResponse?) {
        val sharedPreferences = context.getSharedPreferences("dashboard", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(itemList)
        editor.putString("response_dashboard", json)
        editor.apply()
    }

    private fun saveDataToSharedPreferences(context: Context, responseBody: String) {
        val sharedPreferences = context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        val gson = Gson()
//        val json = gson.toJson(responseBody)
        editor.putString("itemDasboardList", responseBody)
        editor.apply()
    }
    private fun saveDataToSharedPreferencesNotif(context: Context, responseBody: String) {
        val sharedPreferences = context.getSharedPreferences("notif", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("itemNotifList", responseBody)  // Simpan responseBody langsung
        editor.apply()
    }


    fun checkSession(nik: String): Boolean {
        val sharedPreferences = context?.getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", null)
        return userId != null
    }

}
