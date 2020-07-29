package com.example.nurbk.ps.applicationfinal.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Fragments.Admin.AdminHomeFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportFragmentManager.beginTransaction().replace(
                R.id.container,
                AdminHomeFragment()
            )
            .commit()





    }
}
