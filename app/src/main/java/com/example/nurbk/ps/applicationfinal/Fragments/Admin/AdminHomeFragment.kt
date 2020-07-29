package com.example.nurbk.ps.applicationfinal.Fragments.Admin


import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.Models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin_home.view.*


class AdminHomeFragment : Fragment() {


    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog


    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }
    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef
        get() = firebaseInstance
            .document("Users/${FirebaseAuth.getInstance().currentUser!!.uid}")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity!!.toolbar.visibility = View.GONE



        root = inflater.inflate(R.layout.fragment_admin_home, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)
        progressDialog.show()

        getUserInfo { user ->
            if (user.profileImage.isNotEmpty()) {
                GlideApp.with(activity!!)
                    .load(storageInstance.getReference(user.profileImage))
                    .placeholder(R.drawable.ic_interface)
                    .into(root.adminImage)

            }
            root.adminName.text = user.name
            root.adminEmail.text = user.email
            root.adminDec.text = user.description
            root.adminBalance.text = "$${user.price}"
            progressDialog.dismiss()
        }


        root.btnDashboard.setOnClickListener {
            replaceFragment(AdminDashboardFragment())
        }

        root.categoriesAll.setOnClickListener {
            replaceFragment(AdminCategoriesFragment())
        }

        root.productsAll.setOnClickListener {
            replaceFragment(AdminProductsFragment())
        }

        root.btnSetting.setOnClickListener {
            replaceFragment(SettingFragment())
        }



        return root
    }


    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, f)
            .addToBackStack("").commit()
    }

    private fun getUserInfo(getDataUser: (user: Users) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            getDataUser(it.toObject(Users::class.java)!!)
        }
    }


}
