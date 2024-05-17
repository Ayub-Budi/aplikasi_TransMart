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
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

class LoginFragment : Fragment() {

    private val httpClient: OkHttpClient by lazy {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    private fun saveResponseToSharedPreferences(responseJson: String) {
        val sharedPreferences = requireContext().getSharedPreferences("response_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("response_json", responseJson)
        editor.apply()
    }

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

                val progressDialog = ProgressDialog(context)
                progressDialog.setMessage("Loading...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("username", user)
                    .addFormDataPart("passwd", pass)
                    .build()


                Log.d("ini hit usr & passwd", "user ${user} & pass ${pass}")


                val request = Request.Builder()
                    .url("https://hrms.transretail.co.id/aso/checklogin.php")
                    .post(requestBody)
                    .build()

                Log.d("ini hit api", "${request}")


                httpClient.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val responseBody = response.body?.string() // Baca konten respons

                        progressDialog.dismiss() // Hentikan animasi loading

                        if (response.isSuccessful && responseBody != null) {
                            val jsonResponse = JSONObject(responseBody)
                            val error = jsonResponse.getBoolean("error")
                            if (!error) {
                                // Gunakan responseBody yang telah dibaca sebelumnya
                                saveResponseToSharedPreferences(responseBody)
                                // Jika tidak ada kesalahan, lanjutkan dengan tindakan setelah login berhasil
                                activity?.runOnUiThread {
                                    Toast.makeText(context, "Login berhasil", Toast.LENGTH_SHORT).show()
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                }
                            } else {
                                // Jika ada kesalahan, tampilkan pesan kesalahan dari respons API
                                val errorMsg = jsonResponse.getString("error_msg")
                                activity?.runOnUiThread {
                                    Toast.makeText(context, "Login Failed : $errorMsg", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e("API_CALL_ERROR", "Error: ${response.code}")
                            activity?.runOnUiThread {
                                Toast.makeText(context, "Gagal login. Kode status: ${response.code}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.e("API_CALL_ERROR", "Failed to execute request", e)
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            } else {
                Toast.makeText(context, "Mohon isi username dan password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
