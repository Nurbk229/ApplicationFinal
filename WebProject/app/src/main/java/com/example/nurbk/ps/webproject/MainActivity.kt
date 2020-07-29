package com.example.nurbk.ps.webproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.webChromeClient = WebChromeClient()
        webView.loadUrl("https://www.google.com")
        val webSettings = webView.settings
//        webSettings.javaScriptEnabled(true)
        registerUser()
    }


    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()

    }


    private fun registerUser() {
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            Connect.URL_REGISTER,
            Response.Listener {
                try {
                    val jsonObject = JSONObject(it)
                    Toast.makeText(
                        applicationContext,
                        jsonObject.getString("message"),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("ttt", jsonObject.getString("message"))
                } catch (e: Exception) {
                }
            },
            Response.ErrorListener { }) {
            override fun getParams(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map["username"] = "NurBk123"
                map["password"] = "157428663259ddf"
                map["email"] = "nub@gmail.com"
                return map
            }
        }

        val requestQueue = Volley.newRequestQueue(this)

        requestQueue.add(stringRequest)

    }

}


