package com.a213310009ayubbudisantoso.transmart

import android.content.Context
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a213310009ayubbudisantoso.transmart.api.adapter.DasboardItemAdapter
import com.a213310009ayubbudisantoso.transmart.api.model.ClosestItem
import com.a213310009ayubbudisantoso.transmart.api.model.DashboardResponse
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import com.a213310009ayubbudisantoso.transmart.api.services.DasboardEspiredService
import com.a213310009ayubbudisantoso.transmart.api.services.TarikBarangService
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BebasExpiredFragment : Fragment() {
    private lateinit var kembali: ImageView
    private lateinit var pieChart: PieChart
    private lateinit var didataText: TextView
    private lateinit var ditarikText: TextView
    private lateinit var showMoreText: TextView

    private lateinit var adapter: DasboardItemAdapter

    private var didata = 0f // Variabel untuk radius
    private var ditarik = 0f // Variabel untuk radius



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bebas_expired, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dataExpired = view.findViewById<CardView>(R.id.data)
        val menarikExpired = view.findViewById<CardView>(R.id.tarik)
        kembali = view.findViewById(R.id.kembali)
        showMoreText = view.findViewById(R.id.showMore)
        pieChart = view.findViewById(R.id.pieChartDidata)
        didataText = view.findViewById(R.id.diDataId)
        ditarikText = view.findViewById(R.id.diTarikId)




        fetchDataFromAPIDashboardModel()

        fetchDataFromAPIListExpired()

        displaySavedResponse()

        dataExpired.setOnClickListener {
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_dataExpiredFragment2)
        }
        menarikExpired.setOnClickListener {
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_tarikBarangFragment)
        }

        showMoreText.setOnClickListener { findNavController().navigate(R.id.action_bebasExpiredFragment_to_tarikBarangFragment) }
        kembali.setOnClickListener { findNavController().navigate(R.id.action_bebasExpiredFragment_to_homeFragment) }

//         Inisialisasi RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.dExpired)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

// Inisialisasi adapter RecyclerView
        adapter = DasboardItemAdapter(emptyList())

        adapter.setOnItemClickListener(object : DasboardItemAdapter.OnItemClickListener {
            override fun onItemClick(item: ClosestItem) {
                // Navigasi ke DetailItemFragment saat item diklik
                findNavController().navigate(R.id.action_bebasExpiredFragment_to_detailItemFragment)
            }

        })
        // Set adapter pada RecyclerView
        recyclerView.adapter = adapter

        displaySavedResponseD()


    }

    private fun drawPieChart(radiusList: List<Float>, colorsList: List<Int>) {
        val entries = ArrayList<PieEntry>()
        radiusList.forEachIndexed { index, radius ->
            if (index == 0) {
                entries.add(PieEntry(radius, "Didata"))
            } else {
                entries.add(PieEntry(radius, "Ditarik"))
            }
        }
        val dataSet = PieDataSet(entries, "Radius")
        dataSet.colors = colorsList

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()
    }

    private fun fetchDataFromAPIListExpired() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(TarikBarangService::class.java)

        apiService.getListExpired().enqueue(object : Callback<List<TarikBarangModel>> {
            override fun onResponse(call: Call<List<TarikBarangModel>>, response: Response<List<TarikBarangModel>>) {
                if (response.isSuccessful) {
                    val itemList = response.body()
                    itemList?.let {
                        saveDataToSharedPreferences(requireContext(), it)
                        Log.e("TarikBarangFragment", "Dendapatkan data: ${it}")

                    }

                } else {
                    // Tangani kesalahan jika respons tidak berhasil
                    Log.e("TarikBarangFragment", "Gagal mendapatkan data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<TarikBarangModel>>, t: Throwable) {
                // Tangani kesalahan koneksi atau respons gagal
                Log.e("TarikBarangFragment", "Error: ${t.message}")
            }
        })
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
                    val dataListed = dashboardResponse?.dataListed
                    val dataWithdrawn = dashboardResponse?.dataWithdrawn

                    // Ambil nilai radiusA dan radiusB dari respons API
                    val radiusAFromAPI = dataListed?.itemListedToday?.toFloatOrNull() ?: 0f
                    val radiusBFromAPI = dataWithdrawn?.itemWithdrawnToday?.toFloatOrNull() ?: 0f


                    // Tetapkan nilai radiusA dan radiusB dari API ke variabel yang sudah dideklarasikan sebelumnya
                    didata = radiusAFromAPI
                    ditarik = radiusBFromAPI

                    didataText.text = dataListed?.itemListedToday
                    ditarikText.text = dataWithdrawn?.itemWithdrawnToday


                    val radiusA = didata // Variabel untuk radius
                    val radiusB = ditarik // Variabel untuk radius

                    drawPieChart(listOf(radiusA, radiusB), listOf(Color.BLUE, Color.GREEN))


                    Log.d("DashboardData", "didata: ${didata}")
                    Log.d("DashboardData", "ditarik: ${ditarik}")

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

    fun saveDataToSharedPreferences(context: Context, itemList: List<TarikBarangModel>) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(itemList)
        editor.putString("listDataExpired", json)
        editor.apply()
    }

//    fun loadDataFromSharedPreferences(context: Context): List<TarikBarangModel>? {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val gson = Gson()
//        val json = sharedPreferences.getString("listDataExpired", null)
//        val type = object : TypeToken<List<TarikBarangModel>>() {}.type
//        return gson.fromJson(json, type)
//    }

    private fun displaySavedResponse() {
        val sharedPreferences = requireContext().getSharedPreferences("dashboard", Context.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("response_dashboard", "")
        Log.d("ini hit responseJson", "${responseJson}")

        if (!responseJson.isNullOrEmpty()) {
            try {
                val jsonObject = JSONObject(responseJson)
                val data_listed = jsonObject.getJSONObject("data_listed")
                val data_withdrawn = jsonObject.getJSONObject("data_withdrawn")

                val item_listed_today = data_listed.getDouble("item_listed_today").toFloat()
                val item_withdrawn_today = data_withdrawn.getDouble("item_withdrawn_today").toFloat()

                didata = item_listed_today
                ditarik = item_withdrawn_today

                Log.d("ini hit Data", "$item_listed_today dan $item_withdrawn_today")

                val radiusA = didata // Variabel untuk radius
                val radiusB = ditarik // Variabel untuk radius

                drawPieChart(listOf(radiusA, radiusB), listOf(Color.BLUE, Color.GREEN))

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    }

    private fun displaySavedResponseD() {
        val sharedPreferences = requireContext().getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("itemDasboardList", "")
        Log.d("displaySavedResponse", "$responseJson")

        if (!responseJson.isNullOrEmpty()) {
            try {
                val jsonObject = JSONObject(responseJson)
                val closestItemsJsonArray = jsonObject.getJSONArray("closest_items")

                val gson = Gson()
                val itemType = object : TypeToken<List<ClosestItem>>() {}.type
                val itemList: List<ClosestItem> = gson.fromJson(closestItemsJsonArray.toString(), itemType)

                // Display data in RecyclerView if available
                if (itemList.isNotEmpty()) {
                    adapter.setData(itemList)
                } else {
                    Log.d("displaySavedResponse", "No data available")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

}
