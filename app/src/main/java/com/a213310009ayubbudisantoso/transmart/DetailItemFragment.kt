package com.a213310009ayubbudisantoso.transmart

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.util.*

class DetailItemFragment : Fragment() {

    private lateinit var kembali: ImageView
    private lateinit var textNameItem: TextView
    private lateinit var textNoGondala: TextView
    private lateinit var textItemCode: TextView
    private lateinit var textExpiredDate: TextView
    private lateinit var textItemStatus: TextView
    private lateinit var textItemQty: TextView

    private val CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101
    private lateinit var imageUri: Uri
    private lateinit var hasilFoto: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detail_item, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textNameItem = view.findViewById(R.id.itemName)
        textNoGondala = view.findViewById(R.id.noGondalaValue)
        textItemCode = view.findViewById(R.id.kodeBarangValue)
        textExpiredDate = view.findViewById(R.id.tanggalExpiredValue)
        textItemStatus = view.findViewById(R.id.statusItemValue)
        textItemQty = view.findViewById(R.id.jumlahValue)
        hasilFoto = view.findViewById(R.id.hasilFoto)

        val cameraButton: CardView = view.findViewById(R.id.camera)

        cameraButton.setOnClickListener {
            checkCameraPermission()
        }

        kembali = view.findViewById(R.id.kembali)
        kembali.setOnClickListener { requireActivity().onBackPressed() }

        displaySavedResponse()
    }

    private fun displaySavedResponse() {
        val sharedPreferences = requireContext().getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("item", "")

        if (!responseJson.isNullOrEmpty()) {
            try {
                val jsonObject = JSONObject(responseJson)

                val itemName = jsonObject.getString("ie_item_name")
                val noGondola = jsonObject.getString("ie_gondola_no")
                val kodeBarang = jsonObject.getString("ie_item_code")
                val tanggalExpired = jsonObject.getString("ie_expired_date")
                val statusItem = jsonObject.getString("ie_item_status")
                val jumlahItem = jsonObject.getInt("ie_qty")

                textNameItem.text = itemName
                textNoGondala.text = noGondola
                textItemCode.text = kodeBarang
                textExpiredDate.text = tanggalExpired
                textItemStatus.text = statusItem
                textItemQty.text = jumlahItem.toString()

                Log.d("ResponseJson", "$itemName & $noGondola & $tanggalExpired & $statusItem & $jumlahItem")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "com.a213310009ayubbudisantoso.transmart.fileprovider",
                it
            )
            imageUri = photoURI
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (::imageUri.isInitialized) {
                imageUri.let { uri ->
                    resizeAndSetImage(uri)
                }
            } else {
                imageUri = createImageFile()?.toUri()!!
                if (::imageUri.isInitialized) {
                    resizeAndSetImage(imageUri)
                } else {
                    Log.e("DetailItemFragment", "Gagal membuat file gambar")
                }
            }
        }
    }

    private fun resizeAndSetImage(uri: Uri) {
        try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            inputStream?.let {
                val originalBitmap = BitmapFactory.decodeStream(it)
                val resizedBitmap = resizeBitmap(originalBitmap, 800, 800) // Resize to desired dimensions
                hasilFoto.visibility = View.VISIBLE
                hasilFoto.setImageBitmap(resizedBitmap)
                Log.d("ini hasil nya", "$uri")
                it.close()
            }
        } catch (e: Exception) {
            Log.e("DetailItemFragment", "Error loading image", e)
        }
    }

    private fun resizeBitmap(source: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val aspectRatio: Float = source.width.toFloat() / source.height.toFloat()
        val width: Int
        val height: Int

        if (source.width > source.height) {
            width = maxWidth
            height = (maxWidth / aspectRatio).toInt()
        } else {
            height = maxHeight
            width = (maxHeight * aspectRatio).toInt()
        }

        return Bitmap.createScaledBitmap(source, width, height, true)
    }
}
