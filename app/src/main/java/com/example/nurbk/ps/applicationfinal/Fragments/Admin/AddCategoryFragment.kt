package com.example.nurbk.ps.applicationfinal.Fragments.Admin

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_add_catgory.view.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class AddCategoryFragment : Fragment() {

    private val RC_DIALOG_IMAGE = 2
    private val PICK_FROM_GALLERY = 11

    private lateinit var root: View
    private lateinit var category: Category
    private lateinit var documentCatgoey: DocumentReference
    private lateinit var progressDialog: ProgressDialog


    private var imageCategory: Uri? = null

    private val dataCategory by lazy { ArrayList<Category>() }


    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserStorageRefCategory
        get() = storageInstance
            .reference.child(FirebaseAuth.getInstance().uid.toString())
            .child("CategoryPictures")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_add_catgory, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)

        getAllCategory()

        root.btnAddImageCategory.setOnClickListener {
            preimissionImger()

        }

        updateCategory()

        return root
    }

    private fun updateCategory() {
        if (arguments != null) {
            category = arguments!!
                .getParcelable<Category>("categoryUpdate")!!

            root.btnAddCategory.text = "Update Category"
            root.txtNameCategory.setText(category.name)

            GlideApp.with(activity!!)
                .load(storageInstance.getReference(category.icon!!))
                .into(root.btnAddImageCategory)

            root.btnAddCategory.setOnClickListener {
                val nameCategory = root.txtNameCategory.text.toString()


                if (nameCategory.trim().isEmpty()) {
                    root.txtNameCategory.error = "Name Category is Required"
                    root.txtNameCategory.requestFocus()
                    return@setOnClickListener
                }

                for (itemC in dataCategory) {
                    if (category.name == nameCategory) {
                        break
                    }
                    if (itemC.name == nameCategory) {
                        root.txtNameCategory.error = "This Category already exists"
                        root.txtNameCategory.requestFocus()
                        return@setOnClickListener
                    }
                }
                if (nameCategory == category.name!! && imageCategory == null) {
                    Snackbar.make(
                        root, "Not updated because data already exists",
                        Snackbar.LENGTH_LONG
                    ).show()
                    activity!!.onBackPressed()
                    return@setOnClickListener
                }

                uploadUpdateCategory(nameCategory, imageCategory)
            }
        } else {
            getAddCategory()
        }
    }

    private fun addCategory(imageCategory: Uri, nameCategory: String) {
        val id = "${mapOf(
            "category" to firebaseInstance
                .collection("Users").document().id
        )}${getTime()}"
        documentCatgoey = firebaseInstance.collection(
            "Categories"
        ).document(id)

        val selectImageBmp = MediaStore
            .Images.Media.getBitmap(
            activity!!.contentResolver, imageCategory!!
        )
        val outputStream = ByteArrayOutputStream()
        selectImageBmp
            .compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
        val selectedImgByte = outputStream.toByteArray()
        uploadAddCategoryImage(selectedImgByte, nameCategory, id)
    }

    private fun uploadAddCategoryImage(
        selectedImgByte: ByteArray,
        nameCategory: String,
        id: String
    ) {
        progressDialog.show()

        val imagePath = currentUserStorageRefCategory
            .child(nameCategory)
        imagePath.putBytes(selectedImgByte).addOnSuccessListener {

            category = Category(
                id,
                FirebaseAuth.getInstance().uid,
                nameCategory,
                imagePath.path,
                0.0
            )
            documentCatgoey.set(category).addOnSuccessListener {
                progressDialog.dismiss()
                activity!!.onBackPressed()
            }

        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(
                activity!!,
                "Error : ${it.message}",
                Toast.LENGTH_LONG
            ).show()

        }

    }


    private fun getAddCategory() {
        root.btnAddCategory.setOnClickListener {
            val nameCategory = root.txtNameCategory.text.toString()

            if (imageCategory == null) {
                Snackbar.make(
                    root, "Photo is required",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if (nameCategory.trim().isEmpty()) {
                root.txtNameCategory.error = "Name Category is Required"
                root.txtNameCategory.requestFocus()
                return@setOnClickListener
            }

            for (itemC in dataCategory) {

                if (itemC.name == nameCategory) {
                    root.txtNameCategory.error = "This Category already exists"
                    root.txtNameCategory.requestFocus()
                    return@setOnClickListener
                }
            }

            addCategory(imageCategory!!, nameCategory)

        }
    }

    private fun uploadUpdateCategory(nameCategory: String, imageCategory: Uri?) {
        progressDialog.show()

        val id = category.id!!
        documentCatgoey = firebaseInstance.collection(
            "Categories"
        ).document(id)

        if (imageCategory != null) {
            val selectImageBmp = MediaStore
                .Images.Media.getBitmap(
                activity!!.contentResolver, imageCategory!!
            )
            val outputStream = ByteArrayOutputStream()
            selectImageBmp
                .compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val selectedImgByte = outputStream.toByteArray()
            uploadUpdateCategoryImage(selectedImgByte, nameCategory, id)
        } else {
            val newCategory = Category(
                id,
                FirebaseAuth.getInstance().uid,
                nameCategory,
                category.icon,
                category.salary
            )
            documentCatgoey.set(newCategory).addOnSuccessListener {
                progressDialog.dismiss()
                activity!!.onBackPressed()
            }
        }

    }

    private fun uploadUpdateCategoryImage(
        selectedImgByte: ByteArray,
        nameCategory: String,
        id: String
    ) {

        storageInstance
            .reference
            .child(category.icon!!).delete().addOnSuccessListener {
                val imagePath = currentUserStorageRefCategory
                    .child(nameCategory)
                imagePath.putBytes(selectedImgByte)
                    .addOnSuccessListener {
                        val newCategory = Category(
                            id,
                            FirebaseAuth.getInstance().uid,
                            nameCategory,
                            imagePath.path,
                            category.salary
                        )
                        documentCatgoey.set(newCategory).addOnSuccessListener {

                            progressDialog.dismiss()
                            activity!!.onBackPressed()
                        }
                    }
            }
    }


    private fun getAllCategory() {

        firebaseInstance.collection(
                "Categories"
            )
            .addSnapshotListener { querySnapshot,
                                   firebaseFirestoreException ->


                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                dataCategory.removeAll(dataCategory)
                querySnapshot!!.forEach {

                    val category = it.toObject(Category::class.java)
                    if (category.uidUser == FirebaseAuth.getInstance().uid) {
                        dataCategory.add(category)
                    }

                }

            }


    }

    private fun getTime(): String {
        val time = SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.ms")
        return time.format(Date())
    }

    private fun preimissionImger() {
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
        if (requestCode == RC_DIALOG_IMAGE &&
            resultCode == Activity.RESULT_OK
        ) {
            imageCategory = data!!.data!!
            root.btnAddImageCategory.setImageURI(imageCategory)

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
            RC_DIALOG_IMAGE
        )
    }


}
