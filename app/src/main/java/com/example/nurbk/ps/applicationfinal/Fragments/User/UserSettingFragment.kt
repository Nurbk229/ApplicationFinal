package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nurbk.ps.applicationfinal.Fragments.AboutFragment
import com.example.nurbk.ps.applicationfinal.Fragments.EditPasswordFragment
import com.example.nurbk.ps.applicationfinal.Fragments.EditProfileFragment

import com.example.nurbk.ps.applicationfinal.R
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_user_setting.view.*

/**
 * A simple [Fragment] subclass.
 */
class UserSettingFragment : Fragment() {

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.toolbarU.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.txtNameFU.text = "Setting"
        activity!!.toolbarU.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        root = inflater.inflate(R.layout.fragment_user_setting, container, false)

        root.btnEditProfileU.setOnClickListener {
            replaceFragment(EditProfileFragment())
        }
        root.btnEditPasswordU.setOnClickListener {
            replaceFragment(EditPasswordFragment())

        }
        root.btnAboutU.setOnClickListener {
            replaceFragment(AboutFragment())
        }


        return root
    }

    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerU, f)
            .addToBackStack("").commit()
    }
}
