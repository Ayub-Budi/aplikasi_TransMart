package com.a213310009ayubbudisantoso.transmart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a213310009ayubbudisantoso.transmart.api.adapter.TarikBarangAdapter
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import com.a213310009ayubbudisantoso.transmart.api.services.TarikBarangService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TarikBarangFragment : Fragment() {

    private lateinit var adapter: TarikBarangAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tarik_barang, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter RecyclerView
        adapter = TarikBarangAdapter(emptyList())
        recyclerView.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var apiService = retrofit.create(TarikBarangService::class.java)

        apiService.getListExpired().enqueue(object : Callback<List<TarikBarangModel>> {
            override fun onResponse(call: Call<List<TarikBarangModel>>, response: Response<List<TarikBarangModel>>) {
                if (response.isSuccessful) {
                    val itemList = response.body()
//                    itemList?.forEach { item ->
//                        Log.d("Response", item.toString())
//                    }

                    itemList?.let {
                        adapter.setData(it)
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



}