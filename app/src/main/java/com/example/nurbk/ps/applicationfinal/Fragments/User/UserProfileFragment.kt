package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nurbk.ps.applicationfinal.Activities.MapsActivity
import com.example.nurbk.ps.applicationfinal.Activities.RegistrationActivity
import com.example.nurbk.ps.applicationfinal.Fragments.Admin.SettingFragment
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.Models.Users
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*


class UserProfileFragment : Fragment() {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog
    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserDocRef
        get() = firebaseInstance
            .document("Users/${FirebaseAuth.getInstance().currentUser!!.uid}")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.toolbarU.visibility = View.GONE


        root = inflater.inflate(R.layout.fragment_user_profile, container, false)

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)
        progressDialog.show()


        getUserInfo { user ->
            if (user.profileImage.isNotEmpty()) {
                GlideApp.with(activity!!)
                    .load(storageInstance.getReference(user.profileImage))
                    .placeholder(R.drawable.ic_interface)
                    .into(root.imgUserProfile)

            }
            root.tvUserNameProfile.text = user.name
            root.tvEmailProfile.text = user.email


            progressDialog.dismiss()
        }

        root.btnEditU.setOnClickListener {
            replaceFragment(UserSettingFragment())
        }

        root.btnLocation.setOnClickListener {
            val intent = Intent(activity!!, MapsActivity::class.java)
            startActivity(intent)
        }

        root.btnLogOutU.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity!!.startActivity(Intent(activity!!, RegistrationActivity::class.java))
            activity!!.finish()
        }
        root.btnOder.setOnClickListener {
            replaceFragment(UserOderFragment())

        }

        return root
    }

    private fun getUserInfo(getDataUser: (user: Users) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            getDataUser(it.toObject(Users::class.java)!!)
        }
    }


    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerU, f)
            .addToBackStack("").commit()
    }


}


