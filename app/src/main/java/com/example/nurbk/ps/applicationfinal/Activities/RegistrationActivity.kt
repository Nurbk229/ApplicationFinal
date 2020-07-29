package com.example.nurbk.ps.applicationfinal.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Fragments.SignInFragment
import com.example.nurbk.ps.applicationfinal.Fragments.SignUpFragment
import com.example.nurbk.ps.applicationfinal.Adapters.ViewPageAdapter
import com.google.android.material.tabs.TabLayout

import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {
    private val pager by lazy {
        ViewPageAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportFragmentManager.beginTransaction()
                .replace(R.id.viewpager, SignInFragment()).commit()

        pager.addFragment(SignInFragment(), "")
        pager.addFragment(SignUpFragment(), "")
        viewpager.adapter = pager
        tabs.setupWithViewPager(viewpager)

        tabs.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    txtTitle.text = "Sign In"
                } else {
                    txtTitle.text = "Sign Up"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }
}
