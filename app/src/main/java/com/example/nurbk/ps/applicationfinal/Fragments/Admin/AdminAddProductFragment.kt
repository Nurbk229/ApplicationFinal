package com.example.nurbk.ps.applicationfinal.Fragments.Admin


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
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.example.nurbk.ps.applicationfinal.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin_add_product_fragment.view.*
import kotlinx.android.synthetic.main.fragment_admin_add_product_fragment.view.btnAddProduct
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AdminAddProductFragment : Fragment() {

    private val RC_SELECT_IMAGE = 1
    private val PICK_FROM_GALLERY = 11
    private var typePoiImage = -1


    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog
    private lateinit var dataProduct: Product
    private lateinit var product: Product


    private lateinit var currentUserStorageRefProduct: StorageReference
    private lateinit var currentUserRef: DocumentReference

    private val mFirestore by lazy {
        FirebaseFirestore.getInstance()
    }



    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }



    private val newIdChannel = mFirestore
        .collection("Products").document()

    ///
    private val dataImage by lazy {
        arrayListOf("", "", "")
    }

    private val dataImageProduct by lazy {
        ArrayList<String>()
    }

    private val dataCategory by lazy { ArrayList<String>() }

    private val share
        get() =
            activity!!.getSharedPreferences("File", Context.MODE_PRIVATE)

    ////
    private val adapterCategory by lazy {
        ArrayAdapter(
            activity!!,
            android.R.layout.simple_list_item_1,
            dataCategory
        )

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        activity!!.toolbar.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.toolbar.title = "Add Product"
        activity!!.toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        root = inflater.inflate(R.layout.fragment_admin_add_product_fragment, container, false)


        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)

        updateProduct()
        getAllCategory()


        root.btnAddImage1.setOnClickListener {
            typePoiImage = 0
            preimissionImger()
        }
        root.btnAddImage2.setOnClickListener {
            typePoiImage = 1
            preimissionImger()
        }
        root.btnAddImage3.setOnClickListener {
            typePoiImage = 2
            preimissionImger()
        }

        root.btnAddProduct.setOnClickListener {

            val nameProduct = root.txtAddNameProduct.text.toString()
            val descriptionProduct = root.txtAddDescriptionProduct.text.toString()
            val priceProduct = root.txtAddPriceProductA.text.toString()

            if (dataCategory.size == 0) {
                Snackbar.make(
                    root,
                    "Category Required",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            val category = root.optionCategories.selectedItem.toString()


            if (nameProduct.isEmpty()) {
                root.txtAddNameProduct.error = "Name Product Required"
                root.txtAddNameProduct.requestFocus()
                return@setOnClickListener
            }
            if (descriptionProduct.isEmpty()) {
                root.txtAddDescriptionProduct.error = "Description Product Required"
                root.txtAddDescriptionProduct.requestFocus()
                return@setOnClickListener
            }

            if (category.isEmpty()) {
                Snackbar.make(
                    root,
                    "Category Required",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            if (priceProduct.isEmpty()) {
                root.txtAddPriceProductA.error = "Price Product Required"
                root.txtAddPriceProductA.requestFocus()
                return@setOnClickListener
            }
            if (dataImage[0] == "" || dataImage[1] == "" || dataImage[2] == "") {
                Snackbar.make(
                    root,
                    "The product must 3 image",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            uploadProduct(
                nameProduct,
                descriptionProduct,
                priceProduct.toInt(),
                "${share.getString("lat","")
                }${share.getString("lng","")!!}",
                category,
                dataImageProduct,
                mapOf("product" to newIdChannel.id).toString()
            )

        }

        return root
    }

    private fun uploadProduct(
        nameProduct: String,
        descriptionProduct: String,
        priceProduct: Int,
        locationProduct: String,
        category: String,
        dataImageStorage: ArrayList<String>,
        id: String,
    ) {
        progressDialog.show()
        currentUserRef = mFirestore.collection("Products")
            .document(id)

        currentUserStorageRefProduct = storageInstance
            .reference.child(FirebaseAuth.getInstance().uid.toString())
            .child("ProductPictures").child(category)


        for ((t, image) in dataImage.withIndex()) {
            val selectImageBmp = MediaStore
                .Images.Media.getBitmap(
                activity!!.contentResolver, Uri.parse(image)
            )
            val outputStream = ByteArrayOutputStream()
            selectImageBmp
                .compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
            val selectedImgByte = outputStream.toByteArray()


            dataProduct = Product(
                id,
                FirebaseAuth.getInstance().uid.toString(),
                priceProduct,
                nameProduct,
                descriptionProduct,
                4.5f,
                locationProduct,
                category,
                dataImageStorage,
                1,
                1,
                0,
                1
            )
            uploadProductImage(selectedImgByte, t, dataProduct)


        }

    }


    private fun uploadProductImage(
        selectedImgByte: ByteArray, i: Int, prod: Product,
    ) {

        val imagePath = currentUserStorageRefProduct
            .child("${getTime()}$i")

        imagePath.putBytes(selectedImgByte).addOnCompleteListener {
            if (it.isSuccessful) {
                dataImageProduct.add(imagePath.path)
                if (i == 2) {
                    currentUserRef.set(prod)
                    dataImageProduct.removeAll(dataImageProduct)
                    progressDialog.dismiss()
                    activity!!.onBackPressed()
                }
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    activity!!,
                    "Error : ${it.exception!!.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

    }

    //////////
    private fun updateProduct() {
        if (arguments != null) {
            activity!!.toolbar.title = "Update Product"
            root.btnAddProduct.visibility = View.GONE

            setHasOptionsMenu(true)

            val bundle = arguments!!
            product = bundle.getSerializable("productUpdate")!! as Product
            root.txtAddNameProduct.setText(product.product)
            root.txtAddDescriptionProduct.setText(product.description)
            root.txtAddPriceProductA.setText(product.price.toString())
            dataImageProduct.removeAll(dataImageProduct)
            dataImageProduct.addAll(product.imageProduct)

            root.btnAddImage1.scaleType = ImageView.ScaleType.CENTER_CROP
            root.btnAddImage2.scaleType = ImageView.ScaleType.CENTER_CROP
            root.btnAddImage3.scaleType = ImageView.ScaleType.CENTER_CROP

            GlideApp.with(activity!!)
                .load(storageInstance.getReference(dataImageProduct[0]))
                .into(root.btnAddImage1)
            GlideApp.with(activity!!)
                .load(storageInstance.getReference(dataImageProduct[1]))
                .into(root.btnAddImage2)
            GlideApp.with(activity!!)
                .load(storageInstance.getReference(dataImageProduct[2]))
                .into(root.btnAddImage3)

            for (i in 0 until dataCategory.size) {
                if (dataCategory[i] == product.category) {
                    root.optionCategories.setSelection(i)
                    break
                }
            }


        } else {
            root.btnAddProduct.visibility = View.VISIBLE
            setHasOptionsMenu(false)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity!!.menuInflater.inflate(R.menu.edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                val nameProduct = root.txtAddNameProduct.text.toString()
                val descriptionProduct = root.txtAddDescriptionProduct.text.toString()
                val priceProduct = root.txtAddPriceProductA.text.toString()
                val category = root.optionCategories.selectedItem.toString()


                if (nameProduct.isEmpty()) {
                    root.txtAddNameProduct.error = "Name Product Required"
                    root.txtAddNameProduct.requestFocus()
                    return false
                }
                if (descriptionProduct.isEmpty()) {
                    root.txtAddDescriptionProduct.error = "Description Product Required"
                    root.txtAddDescriptionProduct.requestFocus()
                    return false
                }

                if (category.isEmpty()) {
                    Snackbar.make(
                        root,
                        "Category Required",
                        Snackbar.LENGTH_LONG
                    ).show()
                    return false
                }
                if (priceProduct.isEmpty()) {
                    root.txtAddPriceProductA.error = "Price Product Required"
                    root.txtAddPriceProductA.requestFocus()
                    return false
                }
                progressDialog.setMessage("Updating...")
                progressDialog.show()
                currentUserRef = mFirestore.collection(
                        "Products"
                    )
                    .document(product.id)

                currentUserStorageRefProduct = storageInstance
                    .reference

                var countImage = -1
                for (image in dataImage) {
                    if (image != "") {
                        countImage += 1
                    }
                }

                if (dataImage[0] != "" || dataImage[1] != "" || dataImage[2] != "") {
                    for ((i, image) in dataImage.withIndex()) {
                        if (image != "") {
                            val selectImageBmp = MediaStore
                                .Images.Media.getBitmap(
                                activity!!.contentResolver, Uri.parse(image)
                            )
                            val outputStream = ByteArrayOutputStream()
                            selectImageBmp
                                .compress(Bitmap.CompressFormat.JPEG, 20, outputStream)
                            val selectedImgByte = outputStream.toByteArray()

                            dataProduct = Product(
                                product.id,
                                FirebaseAuth.getInstance().uid!!,
                                priceProduct.toInt(),
                                nameProduct,
                                descriptionProduct,
                                4f,
                                "${share.getString("lat","")
                                }${share.getString("lng","")!!}",
                                category,
                                dataImageProduct,
                                1,
                                product.review,
                                product.countSela,
                                product.countRating
                            )
                            currentUserStorageRefProduct.child(dataImageProduct[i]).delete()
                            val imagePath = currentUserStorageRefProduct
                                .child(FirebaseAuth.getInstance().uid.toString())
                                .child("ProductPictures")
                                .child(category)
                                .child("${getTime()}$i")

                            imagePath.putBytes(selectedImgByte).addOnSuccessListener {
                                dataImageProduct[i] = imagePath.path
                                currentUserRef.set(dataProduct)
                                progressDialog.dismiss()
                                if (countImage == i) {
                                    activity!!.onBackPressed()
                                }

                            }

                        }

                    }
                } else {
                    dataProduct = Product(
                        product.id,
                        FirebaseAuth.getInstance().uid!!,
                        priceProduct.toInt(),
                        nameProduct,
                        descriptionProduct,
                        4f,
                        "${share.getString("lat","")
                        }${share.getString("lng","")!!}",
                        category,
                        dataImageProduct,
                        1,
                        product.review,
                        product.countSela,
                        product.countRating
                    )
                    currentUserRef.set(dataProduct)
                    progressDialog.dismiss()
                    activity!!.onBackPressed()
                }



                return true
            }
        }

        return false
    }


    //////
    private fun getAllCategory() {
        progressDialog.show()

        mFirestore.collection(
                "Categories"
            )
            .addSnapshotListener {
                    querySnapshot,
                    firebaseFirestoreException,
                ->

                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                var i = 0
                dataCategory.removeAll(dataCategory)
                querySnapshot!!.forEach {

                    val category = it.toObject(Category::class.java)
                    if (category.uidUser == FirebaseAuth.getInstance().uid) {
                        dataCategory.add(category.name!!)
                    }
                    if (i == querySnapshot.size() - 1) {
                        progressDialog.dismiss()
                        adapterCategory.notifyDataSetChanged()
                    }
                    i++
                }
                if (dataCategory.size == 0) {
                    progressDialog.dismiss()
                }

                root.optionCategories.adapter = adapterCategory
            }

    }


    private fun getTime(): String {
        val time = SimpleDateFormat("yyyy-MM-dd.HH.mm.ss.ms")
        return time.format(Date())
    }

    ////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE &&
            resultCode == Activity.RESULT_OK
        ) {
            when (typePoiImage) {
                0 -> {
                    root.btnAddImage1.setImageURI(data!!.data)
                    root.btnAddImage1.scaleType = ImageView.ScaleType.CENTER_CROP
                    if (activity!!.toolbar.title == "Update Product") {
//                        dataImageProduct.removeAt(0)
                        dataImage[0] = data.data.toString()
                    } else
                        dataImage[0] = data.data.toString()
                }
                1 -> {
                    root.btnAddImage2.setImageURI(data!!.data)
                    root.btnAddImage2.scaleType = ImageView.ScaleType.CENTER_CROP
                    if (activity!!.toolbar.title == "Update Product") {
//                        dataImageProduct.removeAt(0)
                        dataImage[1] = data.data.toString()
                    } else
                        dataImage[1] = data.data.toString()
                }
                2 -> {
                    root.btnAddImage3.setImageURI(data!!.data)
                    root.btnAddImage3.scaleType = ImageView.ScaleType.CENTER_CROP
                    if (activity!!.toolbar.title == "Update Product") {
                        dataImage[2] = data.data.toString()
                    } else
                        dataImage[2] = data.data.toString()
                }
            }
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
            RC_SELECT_IMAGE
        )
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
        grantResults: IntArray,
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

}
