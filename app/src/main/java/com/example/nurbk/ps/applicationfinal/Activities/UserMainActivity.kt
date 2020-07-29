package com.example.nurbk.ps.applicationfinal.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Fragments.User.UserCartFragment
import com.example.nurbk.ps.applicationfinal.Fragments.User.UserHomeFragment
import com.example.nurbk.ps.applicationfinal.Fragments.User.UserProfileFragment
import kotlinx.android.synthetic.main.activity_user_main.*
import me.ibrahimsn.lib.OnItemSelectedListener

class UserMainActivity : AppCompatActivity(), OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)
        setSupportActionBar(toolbarU)
        bottomBar.setOnItemSelectedListener(this)
        replaceFragment(UserHomeFragment())
        txtNameFU.text = "Home"
    }

    override fun onItemSelect(pos: Int) {
        when (pos) {
            0 -> {
                replaceFragment(UserHomeFragment())
                txtNameFU.text = "Home"

            }
            1 -> {
                replaceFragment(UserCartFragment())
                txtNameFU.text = "My Cart"
            }
            2->{
                replaceFragment(UserProfileFragment())
            }

        }

    }

    private fun replaceFragment(f: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerU, f).commit()
    }
}
