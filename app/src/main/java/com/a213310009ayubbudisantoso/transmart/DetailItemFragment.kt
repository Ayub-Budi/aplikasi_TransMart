package com.a213310009ayubbudisantoso.transmart

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a213310009ayubbudisantoso.transmart.api.adapter.TarikBarangAdapter
import com.a213310009ayubbudisantoso.transmart.api.model.TarikBarangModel
import com.a213310009ayubbudisantoso.transmart.api.services.TarikBarangService
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

class DetailItemFragment : Fragment() {

    private lateinit var kembali: ImageView
    private lateinit var textNameItem: TextView
    private lateinit var textNoGondala: TextView
    private lateinit var textItemCode: TextView
    private lateinit var textExpiredDate: TextView
    private lateinit var textItemStatus: TextView
    private lateinit var textItemQty: TextView

//    camera
    private val CAMERA_REQUEST_CODE = 100
    private lateinit var imageUri: Uri

    private lateinit var hasilFoto: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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

        val cameraButton: ImageView = view.findViewById(R.id.camera)

        cameraButton.setOnClickListener {
            openCamera()
        }


        kembali = view.findViewById(R.id.kembali)
        kembali.setOnClickListener { findNavController().navigate(R.id.action_detailItemFragment_to_tarikBarangFragment) }

        // Dapatkan data dari SharedPreferences dengan nama "item"
        val sharedPreferences = requireActivity().getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val itemString = sharedPreferences.getString("item", "")

        Log.d("ini hit responseJson", "${itemString}")
        // Tampilkan data di TextView di tampilan XML
//        val itemTextView: TextView = view.findViewById(R.id.item)
//        itemTextView.text = itemString

        displaySavedResponse()
    }

    private fun displaySavedResponse() {
        val sharedPreferences = requireContext().getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
        val responseJson = sharedPreferences.getString("item", "")
        Log.d("ini hit dipisah", "$responseJson")

        if (!responseJson.isNullOrEmpty()) {
            try {
                val jsonObject = JSONObject(responseJson)

                // Access the fields within the jsonObject using the correct keys
                val itemName = jsonObject.getString("ie_item_name")
                val noGondola = jsonObject.getString("ie_gondola_no")
                val kodeBarang = jsonObject.getString("ie_item_code")
                val tanggalExpired = jsonObject.getString("ie_expired_date")
                val statusItem = jsonObject.getString("ie_item_status")
                val jumlahItem = jsonObject.getInt("ie_qty") // Assuming this is an integer

                textNameItem.text = itemName
                textNoGondala.text = noGondola
                textItemCode.text = kodeBarang
                textExpiredDate.text = tanggalExpired
                textItemStatus.text = statusItem
                textItemQty.text = jumlahItem.toString()




                Log.d("ini hit responseJson", "$itemName & $noGondola & $tanggalExpired & $statusItem & $jumlahItem")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

//    camera
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//            // Handle the image and send it to the API
////            uploadImageToAPI(imageUri)
//            hasilFoto.visibility = View.VISIBLE
//        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val imageBitmap = data.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                hasilFoto.visibility = View.VISIBLE
                hasilFoto.setImageBitmap(imageBitmap)
            } else {
                Log.e("DetailItemFragment", "Bitmap is null")
            }
        }    }

//    private fun uploadImageToAPI(imageUri: Uri) {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://your.api.url/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val service = retrofit.create(TarikBarangService::class.java)
//
//        // Prepare the file to be uploaded
//        val file = File(imageUri.path)
//        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        val body = MultipartBody.Part.createFormData("picture", file.name, requestFile)
//
//        val call = service.uploadImage(body)
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    Log.d("Upload", "Success: ${response.body()?.string()}")
//                } else {
//                    Log.e("Upload", "Failed: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e("Upload", "Error: ${t.message}")
//            }
//        })
//    }


}