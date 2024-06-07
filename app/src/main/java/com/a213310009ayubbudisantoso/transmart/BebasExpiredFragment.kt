package com.a213310009ayubbudisantoso.transmart

import DashboardExpiredModel
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
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

    private lateinit var scrollView: ScrollView
    private lateinit var loadingAnimation: ImageView

    private var nik: String? = null
    private var usp_dept: String? = null




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

        scrollView = view.findViewById(R.id.scrollViewD)
        loadingAnimation = view.findViewById(R.id.loadingAnimation) // Initialize loadingAnimation here


//        scrollView.viewTreeObserver.addOnScrollChangedListener(object : ViewTreeObserver.OnScrollChangedListener {
//            override fun onScrollChanged() {
//                if (!scrollView.canScrollVertically(-1)) {
//                    // The scrollView is at the top
//                    Log.d("ScrollView", "ScrollView is at the top")
//                }
//            }
//        })
        setupTouchListener()

        displaySavedResponseUser()
        fetchDataFromAPIListExpired()
        fetchDataFromAPIDashboardModel()

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

        val uspUser = nik.toString()
        val dpt = usp_dept.toString()
        Log.d("coba ini", "$nik ")
        displaySavedResponseUser()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(TarikBarangService::class.java)

        apiService.getListExpired(uspUser, dpt).enqueue(object : Callback<List<TarikBarangModel>> {
            override fun onResponse(call: Call<List<TarikBarangModel>>, response: Response<List<TarikBarangModel>>) {
                if (response.isSuccessful) {
                    val itemList = response.body()
                    itemList?.let {
                        // Simpan data ke shared preferences setelah menerimanya
                        saveDataToSharedPreferences(requireContext(), it)
                        Log.d("DataListExpierd", "Dapatkan data: $it")
                    }
                } else {
                    // Tangani kesalahan jika respons tidak berhasil
                    Log.e("DataListExpierd", "Gagal mendapatkan data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<TarikBarangModel>>, t: Throwable) {
                // Tangani kesalahan saat koneksi gagal
                Log.e("DataListExpierd", "Error: ${t.message}")
            }
        })
    }

    private fun fetchDataFromAPIDashboardModel() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(DasboardEspiredService::class.java)
        val uspUser = nik.toString()
        val dpt = usp_dept.toString()

        Log.d("DasboardEspired", "$nik ")

        apiService.getDashboardData(uspUser, dpt).enqueue(object : Callback<DashboardResponse> {
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

                    didataText.text = dataListed?.itemListedToday ?: "0"
                    ditarikText.text = dataWithdrawn?.itemWithdrawnToday ?: "0"

                    val radiusA = didata // Variabel untuk radius
                    val radiusB = ditarik // Variabel untuk radius

                    drawPieChart(listOf(radiusA, radiusB), listOf(Color.BLUE, Color.GREEN))

                    Log.d("DashboardData", "didata: $didata")
                    Log.d("DashboardData", "ditarik: $ditarik")

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


        apiService.getDashboardData(uspUser, dpt).enqueue(object : Callback<DashboardResponse> {
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

                    didataText.text = dataListed?.itemListedToday ?: "0"
                    ditarikText.text = dataWithdrawn?.itemWithdrawnToday ?: "0"

                    val radiusA = didata // Variabel untuk radius
                    val radiusB = ditarik // Variabel untuk radius

                    drawPieChart(listOf(radiusA, radiusB), listOf(Color.BLUE, Color.GREEN))

                    Log.d("DashboardData", "didata: $didata")
                    Log.d("DashboardData", "ditarik: $ditarik")

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
                        saveDataToSharedPreferencesListD(requireContext(), it)
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

    private fun saveDataToSharedPreferencesListD(context: Context, responseBody: String) {
        val sharedPreferences = context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("itemDasboardList", responseBody)
        editor.apply()
    }



//    private fun startLoadingAnimation() {
//        loadingAnimation.visibility = View.VISIBLE
//        val rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate)
//        loadingAnimation.startAnimation(rotateAnimation)
//    }

    private fun stopLoadingAnimation() {
        loadingAnimation.clearAnimation()
        loadingAnimation.visibility = View.GONE
    }

    private fun startLoadingAnimation() {
        // Pastikan loadingAnimation sudah diinisialisasi sebelum digunakan
        if (::loadingAnimation.isInitialized) {
            loadingAnimation.visibility = View.VISIBLE

            // Lakukan tindakan animasi tambahan jika diperlukan
            // Contoh: Menjalankan animasi
            loadingAnimation.animate()
                .alpha(1.0f)
                .setDuration(500)
                .withEndAction {
                    // Sembunyikan animasi setelah selesai
                    loadingAnimation.animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .withEndAction {
                            loadingAnimation.visibility = View.GONE
                        }
                }
        } else {
            Log.e("BebasExpiredFragment", "loadingAnimation belum diinisialisasi")
        }
    }

    private fun setupTouchListener() {
        scrollView.setOnTouchListener(object : View.OnTouchListener {
            private var startY = 0f
            private var isPullingDown = false

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            startY = event.y
                            isPullingDown = false
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (!scrollView.canScrollVertically(-1) && event.y > startY) {
                                isPullingDown = true
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            if (isPullingDown) {
                                Log.d("ScrollView", "berhasil tarik ke atas")
//                                Toast.makeText(context, "Berhasil tarik ke atas", Toast.LENGTH_SHORT).show()
                                fetchDataFromAPIDasboardList()
                                displaySavedResponseD()
                                startLoadingAnimation()
                                // Stop the animation after a delay (e.g., 2 seconds)
                                scrollView.postDelayed({
                                    stopLoadingAnimation()
                                }, 2000)
                            }
                        }
                    }
                }
                return false
            }
        })
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

//                nameUser = user.optString("name", "Unknown User")
                nik = user.optString("nik", "Unknown User")
                usp_dept = dbData?.optString("usp_dept", "Unknown User")
//                uspStore = dbData?.optString("usp_store", "Unknown Store") ?: "Unknown Store"


                Log.d("ResponseJson", "uspUser: $nik" +
                        "")
            } catch (e: Exception) {
                Log.e("ResponseJson", "Error parsing response string", e)
            }
        } else {
            Log.d("ResponseJson", "No response data found")
        }
    }



}
