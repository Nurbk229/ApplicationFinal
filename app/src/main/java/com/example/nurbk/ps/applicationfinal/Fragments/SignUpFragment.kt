package com.example.nurbk.ps.applicationfinal.Fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nurbk.ps.applicationfinal.Activities.MainActivity
import com.example.nurbk.ps.applicationfinal.Activities.UserMainActivity
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_sign_up.view.*


class SignUpFragment : Fragment() {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog
    private var emailR = "user.com"


    private val share
        get() =
            activity!!.getSharedPreferences("File", Context.MODE_PRIVATE)


    private val editor
        get() =
            share.edit()


    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val curentUserRef: DocumentReference
        get() = firestore.document("Users/${mAuth.currentUser!!.uid}")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_sign_up, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)


        root.btnSignUp.setOnClickListener {

            val name = root.txtNameSignUp.text.toString().trim()
            val email = root.txtEmailSignUp.text.toString().trim()
            val password = root.txtPasswordSignUp.text.toString().trim()
            val phone = root.txtPhoneSignUp.text.toString().trim()

            if (name.trim().isEmpty()) {
                root.txtNameSignUp.error = "Name Required"
                root.txtNameSignUp.requestFocus()
                return@setOnClickListener
            }
            if (email.trim().isEmpty()) {
                root.txtEmailSignUp.error = "Name Required"
                root.txtEmailSignUp.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                root.txtEmailSignUp.error = "Please Enter a Valid Email"
                root.txtEmailSignUp.requestFocus()
                return@setOnClickListener
            }
            if (!email.contains("user.com") && !email.contains("admin.com")) {
                root.txtEmailSignUp.error = "The email must be user.com or admin.com"
                root.txtEmailSignUp.requestFocus()
                return@setOnClickListener
            }
            if (phone.length < 10) {
                root.txtPhoneSignUp.error = "The phone must be 10 character"
                root.txtPhoneSignUp.requestFocus()
                return@setOnClickListener
            }
            if (phone.trim().isEmpty()) {
                root.txtPhoneSignUp.error = "Phone Required"
                root.txtPhoneSignUp.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                root.txtPasswordSignUp.error =
                    "The password must be more than or equal to 6 characters"
                root.txtPasswordSignUp.requestFocus()
                return@setOnClickListener
            }
            if (password.trim().isEmpty()) {
                root.txtPasswordSignUp.error = "Password Required"
                root.txtPasswordSignUp.requestFocus()
                return@setOnClickListener
            }
            emailR = email.substring(email.indexOf("@", 0)).toLowerCase()

            createNewAccount(name, email, phone, password)
        }

        return root
    }


    private fun createNewAccount(
        name: String,
        email: String,
        phone: String,
        password: String
    ) {

        progressDialog.show()

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val user = Users(
                    mAuth.uid.toString(),
                    name,
                    email,
                    phone,
                    "",
                    password,
                    0,
                    ""
                )

                curentUserRef.set(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                    } else {
                        Log.e("ttt", it.exception.toString())
                    }
                }


                progressDialog.dismiss()

//                if (emailR == "@admin.com") {
//                    startActivity(Intent(activity!!, MainActivity::class.java))
//                    editor.putString("Reg", "@admin.com").apply()
//                } else {
//                    startActivity(Intent(activity!!, UserMainActivity::class.java))
//                    editor.putString("Reg", "@user.com").apply()
//
//                }
                sendEmailVerification()
//                activity!!.finish()

            }.addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(
                    activity!!,
                    it.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    private fun sendEmailVerification() {
        val user = mAuth.currentUser

        user!!.sendEmailVerification().addOnSuccessListener {
            Toast.makeText(activity!!, "Send Verification Email", Toast.LENGTH_LONG).show()
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.viewpager, SignInFragment()).commit()
        }

    }
}




