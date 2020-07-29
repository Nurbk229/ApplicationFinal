package com.example.nurbk.ps.applicationfinal.Fragments


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Users
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_edit_profile.view.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class EditProfileFragment : Fragment(), TextWatcher {
    private val REQUEST_CODE = 1
    private val PICK_FROM_GALLERY = 11

    //////
    private lateinit var progressDialog: ProgressDialog
    private lateinit var root: View
    private lateinit var nameF: String
    private lateinit var emailF: String
    private lateinit var descriptionF: String
    private lateinit var passwordF: String
    private lateinit var users: Users

    //////
    private var imageProfile: Uri? = null

    ////////

    private val mFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val mAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    private val mUser get() = mAuth.currentUser

    private val currentUserDocRef
        get() = mFirestore
            .collection("Users").document(mUser!!.uid)


    //
    private val currentUserStorageRef
        get() = storageInstance
            .reference.child(FirebaseAuth.getInstance().uid.toString())

    private val auth by lazy { FirebaseAuth.getInstance().currentUser }

////////////

    private val share
        get() =
            activity!!.getSharedPreferences("File", Context.MODE_PRIVATE)

    private val dataU by lazy {
        ArrayList<Users>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /////
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
        ////////


        root = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)
        progressDialog.show()

        getUserInfo()
        getAllUser()

        if (share.getString("Reg", "")!! == "@admin.com") {
            root.txtEditDescriptionProfile.visibility = View.VISIBLE
        }
        root.containerPhotoProfile.setOnClickListener {
            PermissionsImage()

        }

        root.btnChangeContainerPhotoProfile.setOnClickListener {
            PermissionsImage()
        }

        root.txtEditNameProfile.addTextChangedListener(this)
        root.txtEditPhoneProfile.addTextChangedListener(this)
        root.txtEditDescriptionProfile.addTextChangedListener(this)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity!!.menuInflater.inflate(R.menu.save, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {

                val name = root.txtEditNameProfile.text.toString()
                val phone = root.txtEditPhoneProfile.text.toString()
                val email = root.txtEditEmailProfile.text.toString()
                val description = root.txtEditDescriptionProfile.text.toString()


                if (name.trim().isEmpty()) {
                    root.txtEditNameProfile.error = "Name Required"
                    root.txtEditNameProfile.requestFocus()
                    return false
                }

                if (phone.trim().isEmpty()) {
                    root.txtEditPhoneProfile.error = "Email Required"
                    root.txtEditPhoneProfile.requestFocus()
                    return false
                }
                if (email.trim().isEmpty()) {
                    root.txtEditEmailProfile.error = "Email Required"
                    root.txtEditEmailProfile.requestFocus()
                    return false
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    root.txtEditEmailProfile.error = "Please Enter a Valid Email"
                    root.txtEditEmailProfile.requestFocus()
                    return false
                }
                if (!email.contains(share.getString("Reg", "")!!)) {
                    root.txtEditEmailProfile.error =
                        "The email must be ${share.getString("Reg", "")!!}"
                    root.txtEditEmailProfile.requestFocus()
                    return false
                }

                for (user in dataU) {
                    if (users.email == email) {
                        break
                    }
                    if (user.email == email) {
                        root.txtEditEmailProfile.error =
                            "This email is not available"
                        root.txtEditEmailProfile.requestFocus()
                        return false
                    }
                }


                uploadData(name, email, phone, description) {
                    progressDialog.dismiss()
                    activity!!.onBackPressed()
                }

                return true
            }
        }
        return false
    }


    private fun uploadData(
        name: String,
        email: String,
        phone: String,
        description: String,
        onBack: () -> Unit
    ) {

        val userPieMap = mutableMapOf<String, Any>()
        userPieMap["name"] = name
        userPieMap["phone"] = phone
        userPieMap["description"] = description
        userPieMap["password"] = passwordF

        if (imageProfile != null) {
            val selectImageBmp = MediaStore
                .Images.Media.getBitmap(
                activity!!.contentResolver, imageProfile
            )

            val outputStream = ByteArrayOutputStream()
            selectImageBmp.compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val selectedImgByte = outputStream.toByteArray()


            uploadProfileImage(selectedImgByte)
            { image ->
                userPieMap["profileImage"] = image
                currentUserDocRef.update(userPieMap)
            }
        }

        progressDialog.show()


        auth.let { task ->
            val userAuth = EmailAuthProvider
                .getCredential(task!!.email!!, passwordF)
            task.reauthenticate(userAuth).addOnCompleteListener {
                if (it.isSuccessful) {


                    task.updateEmail(email).addOnSuccessListener {
                        userPieMap["email"] = email
                        currentUserDocRef.update(userPieMap).addOnSuccessListener {
                                onBack()
                            }
                            .addOnFailureListener { ex ->
                                Toast.makeText(
                                    activity!!,
                                    "Error : ${ex.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressDialog.dismiss()

                            }

                    }.addOnFailureListener { ex ->
                        Toast.makeText(
                            activity!!,
                            "Error : ${ex.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        progressDialog.dismiss()
                    }
                } else {
                    Toast.makeText(
                        activity!!,
                        "Error : ${it.exception!!.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    progressDialog.dismiss()
                }
            }

        }
    }

    private fun uploadProfileImage(
        selectedImgByte: ByteArray,
        onSuccess: (imagePath: String) -> Unit
    ) {
        val imagePath = currentUserStorageRef
            .child("profilePictures/${UUID.nameUUIDFromBytes(selectedImgByte)}")

        imagePath.putBytes(selectedImgByte).addOnCompleteListener {
            if (it.isSuccessful) {

                onSuccess(imagePath.path)
                imageProfile = null
            } else {
                Toast.makeText(
                    activity!!,
                    "Error : ${it.exception!!.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    override fun afterTextChanged(p0: Editable?) {
        
            setHasOptionsMenu(true)

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


    private fun PermissionsImage() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PICK_FROM_GALLERY
                )
            } else {
                addImage()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }

        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            REQUEST_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PICK_FROM_GALLERY ->
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    addImage()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE &&
            resultCode == Activity.RESULT_OK
        ) {

            root.containerPhotoProfile.setImageURI(data!!.data)
            imageProfile = data.data
            setHasOptionsMenu(true)
        }
    }


    private fun getUserInfo() {
        currentUserDocRef.get().addOnSuccessListener {
            users = it.toObject(Users::class.java)!!
            nameF = users.name
            descriptionF = users.description
            passwordF = users.password
            descriptionF = users.description
            emailF = users.email
            if (users.profileImage.isNotEmpty()) {
                GlideApp.with(activity!!)
                    .load(storageInstance.getReference(users.profileImage))
                    .placeholder(R.drawable.ic_interface)
                    .into(root.containerPhotoProfile)
            }

            root.txtEditNameProfile.setText(users.name)
            root.txtEditEmailProfile.setText(users.email)
            root.txtEditPhoneProfile.setText(users.phone)
            root.txtEditDescriptionProfile.setText(users.description)
            progressDialog.dismiss()
        }
    }


    private fun getAllUser() {


        mFirestore.collection(
            "Users"
        ).addSnapshotListener {
                querySnapshot,
                firebaseFirestoreException,
            ->

            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }

            dataU.removeAll(dataU)
            querySnapshot!!.forEach {
                if (querySnapshot.isEmpty) {
                    return@addSnapshotListener
                }
                val user = it.toObject(Users::class.java)
                dataU.add(user)
            }


        }


    }
}