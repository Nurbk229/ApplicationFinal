package com.example.nurbk.ps.webproject

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import kotlinx.android.synthetic.main.activity_download.*

class DownloadActivity : AppCompatActivity() {

    private val PERMISSION_STORAGE_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)



        btnDownload.setOnClickListener {
            getpermission()
        }


    }


    private fun getpermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_APN_SETTINGS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val permission = arrayOf(Manifest.permission.WRITE_APN_SETTINGS)

                requestPermissions(permission, PERMISSION_STORAGE_CODE)
            } else {
                startDownload()
            }
        } else {
            startDownload()
        }
    }

    private fun startDownload() {

        val url = txtLink.text.toString().trim()

        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                    or DownloadManager.Request.NETWORK_MOBILE
        )
        request.setTitle("Download")
        request.setDescription("Downloading file....")
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS,"${System.currentTimeMillis()}")
        val manager= getSystemService(Context.DOWNLOAD_SERVICE)as DownloadManager
        manager.enqueue(request)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            PERMISSION_STORAGE_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload()
                } else {

                }
            }
        }

    }
}
