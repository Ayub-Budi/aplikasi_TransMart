package com.a213310009ayubbudisantoso.transmart

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.a213310009ayubbudisantoso.transmart.api.model.DashboardResponse
import com.a213310009ayubbudisantoso.transmart.api.services.DasboardEspiredService
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var nameUesr: String? = null
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

        val powerOff = view.findViewById<ImageButton>(R.id.powerOff)

        val nameText = view.findViewById<TextView>(R.id.name)
        val storeText = view.findViewById<TextView>(R.id.storeName)

        powerOff.setOnClickListener{
            // Buat dialog konfirmasi
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Konfirmasi")
            builder.setMessage("Apakah Anda yakin ingin keluar?")

            // Tombol untuk mengkonfirmasi
            builder.setPositiveButton("Ya") { dialog, _ ->
                // Tutup aktivitas
                requireActivity().finish()
                dialog.dismiss()
            }

            // Tombol untuk membatalkan
            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

            // Tampilkan dialog
            val dialog = builder.create()
            dialog.show()
        }


        bebasExpired.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_bebasExpiredFragment)
        }

        listBarang.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)

        }
        displaySavedResponse()
        nameText.text = this@HomeFragment.nameUesr
        storeText.text = this@HomeFragment.storeUser

        fetchDataFromAPIDashboardModel()
    }

    private fun displaySavedResponse() {
        val sharedPreferences = requireContext().getSharedPreferences("response_data", Context.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("response_json", "")
        Log.d("ini hit responseJson", "${responseJson}")

        if (!responseJson.isNullOrEmpty()) {
            try {
                val jsonObject = JSONObject(responseJson)
                val userObject = jsonObject.getJSONObject("user")
                val userName = userObject.getString("name")
                val locationName = userObject.getString("locationName")

                nameUesr = userName
                storeUser = locationName

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    fun saveDataToSharedPreferences(context: Context, itemList: DashboardResponse?) {
        val sharedPreferences = context.getSharedPreferences("dashboard", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(itemList)
        editor.putString("response_dashboard", json)
        editor.apply()
    }



    fun fetchDataFromAPIDashboardModel() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(DasboardEspiredService::class.java)

        apiService.getDashboardData().enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful) {
                    val dashboardResponse = response.body()
                    val dataListed = dashboardResponse?.dataListed
                    val dataWithdrawn = dashboardResponse?.dataWithdrawn

                    saveDataToSharedPreferences(requireContext(), dashboardResponse)


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


}