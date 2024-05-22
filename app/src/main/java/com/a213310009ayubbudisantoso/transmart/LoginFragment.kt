package com.a213310009ayubbudisantoso.transmart

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = view.findViewById<TextInputEditText>(R.id.username)
        val password = view.findViewById<TextInputEditText>(R.id.password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()


            if (user.isNotEmpty() && pass.isNotEmpty()) {

                login(user, pass)

            } else {
                Toast.makeText(context, "Mohon isi username dan password", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveResponseToSharedPreferences(responseJson: String) {
        val sharedPreferences = requireContext().getSharedPreferences("response_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("response_json", responseJson.toString())
        editor.apply()
    }
    private fun saveResponseToSharedPreferencesT(responseJson: List<DbData>) {
        val sharedPreferences = requireContext().getSharedPreferences("response_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("dbData", responseJson.toString())
        editor.apply()
    }
    // Assuming this function is within your activity or fragment
    private fun login(username: String, password: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend.transmart.co.id/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create service instance
        val service = retrofit.create(LoginService::class.java)

        // Create request body
        val jsonObject = JsonObject().apply {
            addProperty("username", username)
            addProperty("password", password)
        }

        Log.d("RequestBody", "$username & $password")

        service.login(jsonObject).enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful) {
                    progressDialog.dismiss()
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        val apiData = loginResponse.apiData
                        val dbData = loginResponse.dbData
                        val error = loginResponse.apiData.error
                        val user = loginResponse.apiData.user
                        val usp_store = loginResponse.dbData?.get(0)?.usp_store


//                        merubah ke jeson
                        val loginResponseJson = Gson().toJson(loginResponse)


                        saveResponseToSharedPreferences(loginResponseJson)
                        saveResponseToSharedPreferencesT(dbData)

                        if (error == false){
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
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody ?: "Unknown error"
                    Log.e("LoginError", "Error: $errorMessage")
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Gagal login. Kode status: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                Log.d("API Error", "Failed to send barcode: ${t.message}")
            }
        })

    }



}
