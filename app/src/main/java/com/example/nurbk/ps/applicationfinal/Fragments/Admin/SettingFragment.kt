package com.example.nurbk.ps.applicationfinal.Fragments.Admin


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nurbk.ps.applicationfinal.Activities.RegistrationActivity
import com.example.nurbk.ps.applicationfinal.Fragments.AboutFragment
import com.example.nurbk.ps.applicationfinal.Fragments.EditPasswordFragment
import com.example.nurbk.ps.applicationfinal.Fragments.EditProfileFragment
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setting.view.*


class SettingFragment : Fragment() {
    private lateinit var root: View


    private val share
        get() =
            activity!!.getSharedPreferences("File", Context.MODE_PRIVATE)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /////
        activity!!.toolbar.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.toolbar.title = "Setting"
        activity!!.toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        ////////

        root = inflater.inflate(R.layout.fragment_setting, container, false)


        root.btnEditProfile.setOnClickListener {
            replaceFragment(EditProfileFragment())
        }
        root.btnEditPassword.setOnClickListener {
            replaceFragment(EditPasswordFragment())

        }
        root.btnAbout.setOnClickListener {
            replaceFragment(AboutFragment())
        }


        root.btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity!!.startActivity(Intent(activity!!, RegistrationActivity::class.java))
            activity!!.finish()
        }
        return root
    }

    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .addToBackStack("").commit()
    }
}
