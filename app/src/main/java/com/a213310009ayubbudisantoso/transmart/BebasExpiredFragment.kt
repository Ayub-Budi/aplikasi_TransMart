package com.a213310009ayubbudisantoso.transmart

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import com.a213310009ayubbudisantoso.transmart.api.services.TarikBarangService
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BebasExpiredFragment : Fragment() {
    private lateinit var kembali: ImageView
    private lateinit var pieChart: PieChart

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
        pieChart = view.findViewById(R.id.pieChartDidata)

        val radiusA = 10f // Variabel untuk radius
        val radiusB = 2f // Variabel untuk radius

        drawPieChart(listOf(radiusA, radiusB), listOf(Color.BLUE, Color.GREEN))

        fetchDataFromAPIListExpired()

        dataExpired.setOnClickListener {
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_dataExpiredFragment2)
        }
        menarikExpired.setOnClickListener {
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_tarikBarangFragment)
        }

        kembali.setOnClickListener { findNavController().navigate(R.id.action_bebasExpiredFragment_to_homeFragment) }
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

}
