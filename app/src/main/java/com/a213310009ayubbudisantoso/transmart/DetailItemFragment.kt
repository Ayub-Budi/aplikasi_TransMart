package com.a213310009ayubbudisantoso.transmart

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
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

class DetailItemFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dapatkan data dari SharedPreferences dengan nama "item"
        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val itemString = sharedPreferences.getString("item", "")

        // Tampilkan data di TextView di tampilan XML
        val itemTextView: TextView = view.findViewById(R.id.item)
        itemTextView.text = itemString
    }
}