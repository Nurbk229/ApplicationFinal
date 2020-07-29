package com.example.nurbk.ps.applicationfinal.Fragments


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Users
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_edit_password.view.*

class EditPasswordFragment : Fragment(), TextWatcher {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog
    private lateinit var passwordF: String
    private val auth by lazy { FirebaseAuth.getInstance().currentUser }
    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef
        get() = firebaseInstance
            .document("Users/${FirebaseAuth.getInstance().currentUser!!.uid}")

    private val share
        get() =
            activity!!.getSharedPreferences("File", Context.MODE_PRIVATE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (share.getString("Reg", "")!! == "@admin.com") {
            activity!!.toolbar.visibility = View.VISIBLE
            (activity as AppCompatActivity).supportActionBar!!
                .setDisplayHomeAsUpEnabled(true)
            activity!!.toolbar.title = "Edit Profile"
            activity!!.toolbar.setNavigationOnClickListener {
                activity!!.onBackPressed()
            }
        } else {
            activity!!.toolbarU.visibility = View.VISIBLE
            (activity as AppCompatActivity).supportActionBar!!
                .setDisplayHomeAsUpEnabled(true)
            activity!!.txtNameFU.text = "Edit Profile"
            activity!!.toolbarU.setNavigationOnClickListener {
                activity!!.onBackPressed()
            }
        }
        root = inflater.inflate(R.layout.fragment_edit_password, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)
        getUserInfo()

        root.txtCurrentPassword.addTextChangedListener(this)
        root.txtNewPassword.addTextChangedListener(this)
        root.txtAgainPassword.addTextChangedListener(this)

        return root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity!!.menuInflater.inflate(R.menu.save, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {

                val password = root.txtCurrentPassword.text.toString().trim()
                val passwordNew = root.txtNewPassword.text.toString().trim()
                val passwordAgain = root.txtAgainPassword.text.toString().trim()

                if (password.isEmpty()) {
                    root.txtCurrentPassword.error = "Old Password Required"
                    root.txtCurrentPassword.requestFocus()
                    return false
                }

                if (password != passwordF) {
                    root.txtCurrentPassword.error = "Error Password Required"
                    root.txtCurrentPassword.requestFocus()
                    return false
                }


                if (passwordNew.isEmpty()) {
                    root.txtNewPassword.error = "New Password Required"
                    root.txtNewPassword.requestFocus()
                    return false
                }
                if (passwordAgain.isEmpty()) {
                    root.txtAgainPassword.error = "Again Password Required"
                    root.txtAgainPassword.requestFocus()
                    return false
                }

                if (passwordNew.length < 6) {
                    root.txtNewPassword.error =
                        "The password must be more than or equal to 6 characters"
                    root.txtNewPassword.requestFocus()
                    return false
                }


                if (passwordNew != passwordAgain) {
                    Snackbar.make(
                        root,
                        "Passwords do not match",
                        Snackbar.LENGTH_LONG
                    ).show()
                    return false
                }

                if (passwordNew == passwordF) {
                    root.txtNewPassword.error = "The new password must not equal the old password"
                    root.txtNewPassword.requestFocus()
                    return false
                }

                progressDialog.show()

                auth.let { task ->
                    val userAuth = EmailAuthProvider
                        .getCredential(task!!.email!!, password)
                    task.reauthenticate(userAuth).addOnCompleteListener {
                        if (it.isSuccessful) {

                            task.updatePassword(passwordNew)
                                .addOnSuccessListener {
                                    val userPieMap = mutableMapOf<String, Any>()
                                    userPieMap["password"] = passwordNew
                                    currentUserDocRef.update(userPieMap)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                progressDialog.dismiss()
                                                activity!!.onBackPressed()

                                            } else {
                                                Snackbar.make(
                                                    root,
                                                    "Error : ${task.exception!!.message}",
                                                    Snackbar.LENGTH_LONG
                                                ).show()
                                                progressDialog.dismiss()
                                            }
                                        }
                                }
                                .addOnFailureListener { ex ->
                                    Snackbar.make(
                                        root,
                                        "Error : ${ex.message}",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                    progressDialog.dismiss()
                                }

                        } else {
                            Snackbar.make(
                                root,
                                "Error : ${it.exception!!.message}",
                                Snackbar.LENGTH_LONG
                            ).show()
                            progressDialog.dismiss()
                        }
                    }

                }
            }
        }
        return false
    }


    override fun afterTextChanged(p0: Editable?) {
        setHasOptionsMenu(true)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }


    private fun getUserInfo() {
        currentUserDocRef.get().addOnSuccessListener {
            val users = it.toObject(Users::class.java)!!
            passwordF = users.password

        }
    }

}
