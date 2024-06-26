package com.a213310009ayubbudisantoso.transmart

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.a213310009ayubbudisantoso.transmart.api.model.DbData
import com.a213310009ayubbudisantoso.transmart.api.model.LoginResponse
import com.a213310009ayubbudisantoso.transmart.api.services.LoginService
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginFragment : Fragment() {

    private lateinit var username: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var btnLogin: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = view.findViewById(R.id.username)
        password = view.findViewById(R.id.password)
        btnLogin = view.findViewById(R.id.btn_login)

        btnLogin.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            if (user.isNotEmpty() && pass.isNotEmpty()) {
                login(user, pass)
            } else {
                Toast.makeText(context, "Mohon isi username dan password", Toast.LENGTH_SHORT).show()
            }
        }

        // Memanggil fungsi validasi input
        validateInputs()
    }

    private fun saveResponseToSharedPreferences(responseJson: String) {
        val sharedPreferences = requireContext().getSharedPreferences("response_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("response_json", responseJson)
        editor.apply()
    }

    private fun saveResponseToSharedPreferencesT(responseJson: List<DbData>) {
        val sharedPreferences = requireContext().getSharedPreferences("response_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("dbData", Gson().toJson(responseJson))
        editor.apply()
    }

    private fun login(username: String, password: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginService::class.java)

        val jsonObject = JsonObject().apply {
            addProperty("username", username)
            addProperty("password", password)
        }

        service.login(jsonObject).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val apiData = loginResponse.apiData
                        val dbData = loginResponse.dbData
                        val error = apiData.error
                        val user = apiData.user
                        val usp_store = dbData?.get(0)?.usp_store

                        val loginResponseJson = Gson().toJson(loginResponse)
                        saveResponseToSharedPreferences(loginResponseJson)
                        saveResponseToSharedPreferencesT(dbData ?: listOf())

                        if (!error) {
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            }
                        }

                        Log.d("LoginSuccess", "API Data: $apiData")
                        Log.d("LoginSuccess", "DB Data: $dbData")
                        Log.d("LoginSuccess", "DB Data: $error")
                    } else {
                        Log.e("LoginError", "Response body is null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("LoginError", "Error: $errorBody")
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Gagal login. Kode status: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                Log.d("API Error", "Failed to send barcode: ${t.message}")
            }
        })
    }

    private fun validateInputs() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                checkInputs()
            }
        }

        username.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        // Memanggil fungsi untuk mengecek input setelah layout telah ditampilkan
        checkInputs()
    }

    private fun checkInputs() {
        val allFieldsNotEmpty = username.text?.isNotEmpty() ?: false &&
                password.text?.isNotEmpty() ?: false

        // Atur status tombol dan warna latar belakang
        btnLogin.isEnabled = allFieldsNotEmpty
        btnLogin.setBackgroundResource(if (allFieldsNotEmpty) R.drawable.button_shape else R.drawable.button_shape_grey)
        btnLogin.setTextColor(ContextCompat.getColor(requireContext(), if (allFieldsNotEmpty) R.color.white else R.color.darkGrey))
    }
}
