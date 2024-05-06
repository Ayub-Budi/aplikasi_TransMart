package com.a213310009ayubbudisantoso.transmart

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.a213310009ayubbudisantoso.transmart.api.model.BebasExpiredModel
import com.a213310009ayubbudisantoso.transmart.api.services.BebasExpiredService
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class BebasExpiredFragment : Fragment() {
    private lateinit var kembali: ImageView

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

        dataExpired.setOnClickListener{
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_dataExpiredFragment2)
        }
        menarikExpired.setOnClickListener{
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_tarikBarangFragment)
        }

        kembali.setOnClickListener { findNavController().navigate(R.id.action_bebasExpiredFragment_to_homeFragment) }

    }
}
