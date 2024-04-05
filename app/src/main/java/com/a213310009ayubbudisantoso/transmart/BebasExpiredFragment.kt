package com.a213310009ayubbudisantoso.transmart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.app.DatePickerDialog
import android.text.Editable
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import java.util.*
import java.text.SimpleDateFormat

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bebas_expired, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi EditText
        noGondalaEditText = view.findViewById(R.id.no_gondala)
        kodeEditText = view.findViewById(R.id.kode)
        tglEditText = view.findViewById(R.id.tgl)
        itmEditText = view.findViewById(R.id.itm)
        statusEditText = view.findViewById(R.id.status)
        jumEditText = view.findViewById(R.id.jum)
        planeEditText = view.findViewById(R.id.plane)
        // Inisialisasi tombol kembali
        kembali = view.findViewById(R.id.kembali)
        // Inisialisasi Tanggal
        btnTgl = view.findViewById(R.id.btnTgl)
        //Menjalankan Tanggal
        calendar = Calendar.getInstance()
        // Inisialisasi scanner code
        btnScan = view.findViewById(R.id.btnScan)
        //menjalankan scanner code
        btnScan.setOnClickListener { scanner() }
        // Inisialisasi scanner gondala
        btnScanGondala = view.findViewById(R.id.btnScanGondala)
        //menjalankan scanner gondala
        btnScanGondala.setOnClickListener { scanner2() }

        //Tombol simpan
        val simpanButton: Button = view.findViewById(R.id.simpan)
        simpanButton.setOnClickListener {
            showDataPopup()
        }

        kembali.setOnClickListener {
            findNavController().navigate(R.id.action_bebasExpiredFragment_to_homeFragment)
        }

        btnTgl.setOnClickListener {
            showDatePickerDialog()
        }
        tglEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Pastikan R.array.plane ada di dalam folder res/values
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.plane,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planeEditText.setAdapter(adapter);

        kodeEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Tidak diperlukan untuk operasi ini
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak diperlukan untuk operasi ini
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                itmEditText.text = "gula"
                statusEditText.text = "Returnable"
                if (s.isNullOrEmpty()) {
                    itmEditText.text = "data not yet available"
                    statusEditText.text = "data not yet available"
                }
            }
        })
    }

    //Scann kode barng
    private fun scanner() {
        val options = ScanOptions()
        options.setPrompt("Volume up to Flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        // Pastikan StartScan adalah kelas yang benar untuk mengambil hasil scan barcode
        options.captureActivity = StartScan::class.java
        launcher.launch(options)
    }

    private var launcher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            // Set the result contents to the input text with ID "kode"
            kodeEditText.setText(result.contents)
        }
    }

    //scann nomer gondala
    private fun scanner2() {
        val options = ScanOptions()
        options.setPrompt("Volume up to Flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        // Pastikan StartScan adalah kelas yang benar untuk mengambil hasil scan barcode
        options.captureActivity = StartScan::class.java
        launcher2.launch(options)
    }

    private var launcher2 = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
        if (result.contents != null) {
            // Set the result contents to the input text with ID "kode"
            noGondalaEditText.setText(result.contents)
        }
    }

    //konfrimasi data simpan
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
                Toast.makeText(context, "Data berhasil tersimpan", Toast.LENGTH_SHORT).show()

                reset()

            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
    // Reset EditText
    private fun reset() {
        noGondalaEditText.setText("")
        kodeEditText.setText("")
        tglEditText.setText("")
        itmEditText.setText("data not yet available")
        statusEditText.setText("data not yet available")
        jumEditText.setText("")
    }

    //Tanggal
    private fun showDatePickerDialog() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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
        val myFormat = "dd/MM/yyyy" // Tanggal format
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tglEditText.setText(sdf.format(calendar.time))
    }

}