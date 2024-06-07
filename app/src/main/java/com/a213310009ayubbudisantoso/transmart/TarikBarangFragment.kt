package com.a213310009ayubbudisantoso.transmart

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a213310009ayubbudisantoso.transmart.api.adapter.TarikBarangAdapter
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import com.a213310009ayubbudisantoso.transmart.api.services.TarikBarangService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TarikBarangFragment : Fragment() {

    private lateinit var adapter: TarikBarangAdapter
    private lateinit var kembali: ImageView

//    private lateinit var scrollView: NestedScrollView
    private lateinit var scrollView: RecyclerView
    private lateinit var loadingAnimation: ImageView

    private var nik: String? = null
    private var usp_dept: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarik_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kembali = view.findViewById(R.id.kembali)
        kembali.setOnClickListener { findNavController().navigate(R.id.action_tarikBarangFragment_to_bebasExpiredFragment) }

//        scrollView = view.findViewById(R.id.scrollViewL)
        scrollView = view.findViewById(R.id.recyclerView)
        loadingAnimation = view.findViewById(R.id.loadingAnimation)

        setupTouchListener()

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter RecyclerView
        adapter = TarikBarangAdapter(emptyList())

        adapter.setOnItemClickListener(object : TarikBarangAdapter.OnItemClickListener {
            override fun onItemClick(item: TarikBarangModel) {
                // Navigasi ke DetailItemFragment saat item diklik
                findNavController().navigate(R.id.action_tarikBarangFragment_to_detailItemFragment)
            }
        })

        recyclerView.adapter = adapter

        displaySavedResponse()

    }



    private fun displaySavedResponse() {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("listDataExpired", "")

        // Mengonversi JSON string kembali menjadi objek List<TarikBarangModel>
        val itemType = object : TypeToken<List<TarikBarangModel>>() {}.type
        val itemList: List<TarikBarangModel> = Gson().fromJson(responseJson, itemType)

        // Menampilkan data di RecyclerView jika ada
        if (itemList.isNotEmpty()) {
            adapter.setData(itemList)
        } else {
            // Tangani kasus di mana tidak ada data yang tersimpan
            Log.d("displaySavedResponse", "Tidak ada data yang tersimpan")
        }
    }
    private fun setupTouchListener() {
        scrollView.setOnTouchListener(object : View.OnTouchListener {
            private var startY = 0f
            private var isPullingDown = false

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    Log.d("ScrollView", "Touch event: ${event.action}, Y: ${event.y}")
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            startY = event.y
                            isPullingDown = false
                            Log.d("ScrollView", "ACTION_DOWN at Y: $startY")
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (!scrollView.canScrollVertically(-1) && event.y > startY) {
                                isPullingDown = true
                                Log.d("ScrollView", "ACTION_MOVE, Pulling down")
                            }
                            return true
                        }
                        MotionEvent.ACTION_UP -> {
                            if (isPullingDown) {
                                Log.d("ScrollView", "ACTION_UP, berhasil tarik ke atas")
//                                Toast.makeText(context, "Berhasil tarik ke atas", Toast.LENGTH_SHORT).show()
                                fetchDataFromAPIListExpired()
                                displaySavedResponse()
                                startLoadingAnimation()
                                scrollView.postDelayed({
                                    stopLoadingAnimation()
                                }, 2000)
                            }
                            isPullingDown = false
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

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

    private fun fetchDataFromAPIListExpired() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(TarikBarangService::class.java)
        displaySavedResponseUser()

        val uspUser = nik.toString()
        val dpt = usp_dept.toString()

        apiService.getListExpired(uspUser, dpt).enqueue(object : Callback<List<TarikBarangModel>> {
            override fun onResponse(call: Call<List<TarikBarangModel>>, response: Response<List<TarikBarangModel>>) {
                if (response.isSuccessful) {
                    val itemList = response.body()
                    itemList?.let {
                        saveDataToSharedPreferences(requireContext(), it)
                        Log.d("ListExpired", "Dendapatkan data: ${it}")

                    }

                } else {
                    // Tangani kesalahan jika respons tidak berhasil
                    Log.e("ListExpired", "Gagal mendapatkan data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<TarikBarangModel>>, t: Throwable) {
                Log.e("YourFragment", "Error: ${t.message}")
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
                usp_dept = dbData?.optString("nik", "Unknown User")
//                nik = dbData?.optString("nik", "Unknown Store") ?: "Unknown Store"

                Log.d("ResponseJson", "uspUser: $nik")
            } catch (e: Exception) {
                Log.e("ResponseJson", "Error parsing response string", e)
            }
        } else {
            Log.d("ResponseJson", "No response data found")
        }
    }

}