package com.a213310009ayubbudisantoso.transmart

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.a213310009ayubbudisantoso.transmart.api.model.BebasExpiredModel
import com.a213310009ayubbudisantoso.transmart.api.services.BebasExpiredService
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class BebasExpiredFragment : Fragment() {

    private lateinit var btnScan: ImageButton
    private lateinit var btnScanGondala: ImageButton
    private lateinit var noGondalaEditText: EditText
    private lateinit var kodeEditText: EditText
    private lateinit var tglEditText: EditText
    private lateinit var itmEditText: TextView
    private lateinit var statusEditText: TextView
    private lateinit var jumEditText: EditText
    private lateinit var planeEditText: Spinner
    private lateinit var btnTgl: ImageButton
    private lateinit var calendar: Calendar
    private lateinit var kembali: ImageView

    private lateinit var apiService: BebasExpiredService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bebas_expired, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.40.6.195:3000") // Change with your API base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(BebasExpiredService::class.java)

        // Initialize views
        noGondalaEditText = view.findViewById(R.id.no_gondala)
        kodeEditText = view.findViewById(R.id.kode)
        tglEditText = view.findViewById(R.id.tgl)
        itmEditText = view.findViewById(R.id.itm)
        statusEditText = view.findViewById(R.id.status)
        jumEditText = view.findViewById(R.id.jum)
        planeEditText = view.findViewById(R.id.plane)
        kembali = view.findViewById(R.id.kembali)
        btnTgl = view.findViewById(R.id.btnTgl)
        calendar = Calendar.getInstance()
        btnScan = view.findViewById(R.id.btnScan)
        btnScanGondala = view.findViewById(R.id.btnScanGondala)

        // Button listeners
        btnScan.setOnClickListener { scanner() }
        btnScanGondala.setOnClickListener { scanner2() }
        btnTgl.setOnClickListener { showDatePickerDialog() }
        tglEditText.setOnClickListener { showDatePickerDialog() }
        kembali.setOnClickListener { findNavController().navigate(R.id.action_bebasExpiredFragment_to_homeFragment) }

        // Spinner adapter
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.plane,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planeEditText.adapter = adapter

        kodeEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                itmEditText.text = "gula"
                statusEditText.text = "Returnable"
                if (s.isNullOrEmpty()) {
                    itmEditText.text = "data not yet available"
                    statusEditText.text = "data not yet available"
                }
            }
        })

        // Save button click listener
        val simpanButton: Button = view.findViewById(R.id.simpan)
        simpanButton.setOnClickListener { simpan() }
    }

    private fun scanner() {
        val options = ScanOptions()
        options.setPrompt("Volume up to Flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = StartScan::class.java
        launcher.launch(options)
    }

    private var launcher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            kodeEditText.setText(result.contents)
        }
    }

    private fun scanner2() {
        val options = ScanOptions()
        options.setPrompt("Volume up to Flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = StartScan::class.java
        launcher2.launch(options)
    }

    private var launcher2 = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            noGondalaEditText.setText(result.contents)
        }
    }

    private fun simpan() {
        showDataPopup()
    }

    private fun showDataPopup() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_data_popup, null)

        // Set nilai ke TextView di dialogView
        dialogView.findViewById<TextView>(R.id.noGondalaValue).text = noGondalaEditText.text
        dialogView.findViewById<TextView>(R.id.kodeBarangValue).text = kodeEditText.text
        dialogView.findViewById<TextView>(R.id.tanggalExpiredValue).text = tglEditText.text
        dialogView.findViewById<TextView>(R.id.namaItemValue).text = itmEditText.text
        dialogView.findViewById<TextView>(R.id.statusItemValue).text = statusEditText.text
        dialogView.findViewById<TextView>(R.id.jumlahValue).text = jumEditText.text
        dialogView.findViewById<TextView>(R.id.planeValue).text = planeEditText.selectedItem.toString()

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Bebas Expired Data")
            .setView(dialogView)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                sendDataToApi()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }


    private fun sendDataToApi() {
        val data = BebasExpiredModel().apply {
            gondalaNumber = noGondalaEditText.text.toString()
            itemCode = kodeEditText.text.toString()
            itemName = itmEditText.text.toString()
            statusItem = statusEditText.text.toString()
            expiredDate = tglEditText.text.toString() // Keep as string
            itemAmount = jumEditText.text.toString().toInt()
            iconePlane = planeEditText.selectedItem.toString()
            createBy = "ayub"
            updateBy = "ayub"
            storecode = 123
        }

        apiService.postData(data).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Data berhasil tersimpan", Toast.LENGTH_SHORT).show()
                    reset()
                } else {
                    Toast.makeText(context, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()

                    val errorMessage = "Gagal menyimpan data. Kode status: ${response.code()}, Pesan: ${response.message()}"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    Log.e("API_CALL_ERROR", errorMessage)


                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_CALL_ERROR", "Terjadi kesalahan: ${t.message}", t)

            }
        })
    }


    private fun reset() {
        noGondalaEditText.setText("")
        kodeEditText.setText("")
        tglEditText.setText("")
        itmEditText.setText("data not yet available")
        statusEditText.setText("data not yet available")
        jumEditText.setText("")
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }

        DatePickerDialog(
            requireContext(), dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tglEditText.setText(sdf.format(calendar.time))
    }
}
