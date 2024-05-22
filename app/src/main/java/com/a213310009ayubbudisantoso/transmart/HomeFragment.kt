package com.a213310009ayubbudisantoso.transmart

import DashboardExpiredModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.a213310009ayubbudisantoso.transmart.api.model.DashboardResponse
import com.a213310009ayubbudisantoso.transmart.api.services.DasboardEspiredService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var nameUser: String? = null
    private var storeUser: String? = null

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

        //perbaiki
        val nameText = view.findViewById<TextView>(R.id.name)
        val storeText = view.findViewById<TextView>(R.id.storeName)


        val buttonShowBottomSheet = view.findViewById<ImageView>(R.id.buttonShowBottomSheet)
        buttonShowBottomSheet.setOnClickListener {
            val bottomSheet = ProfileBottomSheetFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        bebasExpired.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_bebasExpiredFragment)
        }

        listBarang.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }

        displaySavedResponseUser()
        nameText.text = this.nameUser
        storeText.text = this.storeUser

        fetchDataFromAPIDashboardModel()
        fetchDataFromAPIDasboardList()
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
                storeUser = dbData?.optString("ms_name", "Unknown Store") ?: "Unknown Store"

                Log.d("ResponseJson", "Name: $nameUser, Store: $storeUser")
            } catch (e: Exception) {
                Log.e("ResponseJson", "Error parsing response string", e)
            }
        } else {
            Log.d("ResponseJson", "No response data found")
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

    private fun fetchDataFromAPIDashboardModel() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(DasboardEspiredService::class.java)

        apiService.getDashboardData().enqueue(object : Callback<DashboardResponse> {
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
                    Log.e("BebasExpiredFragment", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                // Tangani kesalahan koneksi atau respons gagal
                Log.e("BebasExpiredFragment", "Error: ${t.message}")
            }
        })
    }


    private fun fetchDataFromAPIDasboardList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://backend.transmart.co.id/apiMobile/dashboard-expiringSoon"
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
    private fun parseAndLogApiResponse(responseBody: String) {
        val gson = Gson()
        val apiResponse = gson.fromJson(responseBody, DashboardExpiredModel::class.java)
        Log.d("HomeFragment", "Total item nearing expiration: ${apiResponse.totalItemNearingExpiration}")
        Log.d("HomeFragment", "Nearest expiration date: ${apiResponse.nearestExpirationDate}")
        apiResponse.closestItems?.forEach {
            Log.d("HomeFragment", "Item Name: ${it.ieItemName}, Remaining Days: ${it.remainingDays}")
        }
    }



    private fun saveDataToSharedPreferences(context: Context, responseBody: String) {
        val sharedPreferences = context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("itemDasboardList", responseBody)
        editor.apply()
    }
}
