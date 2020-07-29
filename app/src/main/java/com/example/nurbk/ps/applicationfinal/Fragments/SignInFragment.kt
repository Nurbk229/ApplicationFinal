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
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.fragment_sign_in.view.*


class SignInFragment : Fragment() {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog
    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val share
        get() =
            activity!!.getSharedPreferences("File", Context.MODE_PRIVATE)


    private val editor
        get() =
            share.edit()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_sign_in, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)

        root.btnLogIn.setOnClickListener {

            val email = root.txtSignInEmail.text!!.trim().toString()
            val password = root.txtSignInPassword.text!!.trim().toString()



            if (email.isEmpty()) {
                root.txtSignInEmail.error = "Name Required"
                root.txtSignInEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                root.txtSignInEmail.error = "Please Enter a Valid Email"
                root.txtSignInEmail.requestFocus()
                return@setOnClickListener
            }
            if (!email.contains("user.com") && !email.contains("admin.com")) {
                root.txtSignInEmail.error = "The email must be user.com or admin.com"
                root.txtSignInEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                root.txtSignInPassword.error = "6 Char Required"
                root.txtSignInPassword.requestFocus()
                return@setOnClickListener
            }

            signIn(email, password)
        }



        return root
    }

    private fun signIn(email: String, password: String) {

        progressDialog.show()
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
//                if (email.contains("@admin.com")) {
//                    startActivity(Intent(activity!!, MainActivity::class.java))
//                    editor.putString("Reg", "@admin.com").apply()
//                } else if (email.contains("@user.com")) {
//                    startActivity(Intent(activity!!, UserMainActivity::class.java))
//                    editor.putString("Reg", "@user.com").apply()
//
//                }
//                activity!!.finish()
                emailVerify()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    activity!!,
                    task.exception!!.toString(),
                    Toast.LENGTH_LONG
                ).show()
                Log.e("ttt", task.exception!!.toString())
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser?.uid != null) {
            if (share.getString("Reg", "")!! == "@admin.com") {
                val mainActivityIntent = Intent(activity!!, MainActivity::class.java)
                startActivity(mainActivityIntent)
                activity!!.finish()
            } else if (share.getString("Reg", "")!! == "@user.com") {
                val mainActivityIntent = Intent(activity!!, UserMainActivity::class.java)
                startActivity(mainActivityIntent)
                activity!!.finish()
            }
        }
    }


    private fun emailVerify() {
        val user = mAuth.currentUser

        if (user!!.isEmailVerified) {
            if (share.getString("Reg", "")!! == "@admin.com") {
                val mainActivityIntent = Intent(activity!!, MainActivity::class.java)
                startActivity(mainActivityIntent)
                activity!!.finish()
            } else if (share.getString("Reg", "")!! == "@user.com") {
                val mainActivityIntent = Intent(activity!!, UserMainActivity::class.java)
                startActivity(mainActivityIntent)
                activity!!.finish()
            }
        } else {
            Toast.makeText(activity!!, "Pleas Verification Email", Toast.LENGTH_LONG).show()
        }

    }

}
