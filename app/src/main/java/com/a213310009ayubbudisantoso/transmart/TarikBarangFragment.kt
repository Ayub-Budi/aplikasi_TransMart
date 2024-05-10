package com.a213310009ayubbudisantoso.transmart

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a213310009ayubbudisantoso.transmart.api.adapter.TarikBarangAdapter
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import com.a213310009ayubbudisantoso.transmart.api.services.TarikBarangService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TarikBarangFragment : Fragment() {

    private lateinit var adapter: TarikBarangAdapter
    private lateinit var kembali: ImageView

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
        // Load data dari penyimpanan lokal
//        val itemList = loadDataFromSharedPreferences(requireContext())
//        itemList?.let {
//            adapter.setData(it)
//        }

//        fetchDataFromAPI()

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

//    private fun fetchDataFromAPI() {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://backend.transmart.co.id/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val apiService = retrofit.create(TarikBarangService::class.java)
//
//        apiService.getListExpired().enqueue(object : Callback<List<TarikBarangModel>> {
//            override fun onResponse(call: Call<List<TarikBarangModel>>, response: Response<List<TarikBarangModel>>) {
//                if (response.isSuccessful) {
//                    val itemList = response.body()
//                    itemList?.let {
//                        adapter.setData(it)
//                        saveDataToSharedPreferences(requireContext(), it)
//                    }
//
//                } else {
//                    // Tangani kesalahan jika respons tidak berhasil
//                    Log.e("TarikBarangFragment", "Gagal mendapatkan data: ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<TarikBarangModel>>, t: Throwable) {
//                // Tangani kesalahan koneksi atau respons gagal
//                Log.e("TarikBarangFragment", "Error: ${t.message}")
//            }
//        })
//    }

//    fun saveDataToSharedPreferences(context: Context, itemList: List<TarikBarangModel>) {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//        val gson = Gson()
//        val json = gson.toJson(itemList)
//        editor.putString("listDataExpired", json)
//        editor.apply()
//    }
//
//    fun loadDataFromSharedPreferences(context: Context): List<TarikBarangModel>? {
//        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val gson = Gson()
//        val json = sharedPreferences.getString("listDataExpired", null)
//        val type = object : TypeToken<List<TarikBarangModel>>() {}.type
//        return gson.fromJson(json, type)
//    }

}