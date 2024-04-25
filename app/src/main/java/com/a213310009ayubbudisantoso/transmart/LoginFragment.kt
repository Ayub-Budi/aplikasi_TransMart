package com.a213310009ayubbudisantoso.transmart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import java.io.IOException

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = view.findViewById<TextInputEditText>(R.id.username)
        val password = view.findViewById<TextInputEditText>(R.id.password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)

        btnLogin.setOnClickListener {
            val usernameInput = username.text.toString()
            val passwordInput = password.text.toString()

            // Kirim permintaan ke server untuk proses autentikasi
            authenticateUser(usernameInput, passwordInput)
        }
    }

    private fun authenticateUser(username: String, password: String) {
        // Ganti BASE_URL dengan URL API Anda
        val BASE_URL = "https://hrms.transretail.co.id"

        val client = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(BASE_URL + "login")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Tangani kegagalan koneksi
                activity?.runOnUiThread {
                    Toast.makeText(context, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                // Proses respons dari server
                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        // Berhasil login
                        Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        // Gagal login
                        Toast.makeText(context, "Username atau password salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
