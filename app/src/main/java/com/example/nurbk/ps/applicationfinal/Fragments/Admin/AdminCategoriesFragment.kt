package com.example.nurbk.ps.applicationfinal.Fragments.Admin


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nurbk.ps.applicationfinal.Adapters.CategoriesAdapter
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dailog_item.*
import kotlinx.android.synthetic.main.fragment_admin_categories.view.*

import kotlin.collections.ArrayList

class AdminCategoriesFragment : Fragment(), CategoriesAdapter.OnClickItemListener {

    private lateinit var root: View
    private lateinit var dialog: Dialog


    private lateinit var progressDialog: ProgressDialog

    val dataCategory by lazy { ArrayList<Category>() }


    private val dataImageProduct by lazy {
        ArrayList<String>()
    }


    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }


    private val adapter by lazy {
        CategoriesAdapter(activity!!, dataCategory, this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        activity!!.toolbar.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.toolbar.title = "Categories"
        activity!!.toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        root = inflater.inflate(R.layout.fragment_admin_categories, container, false)

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)


        root.rcDataCategories.adapter = adapter
        root.rcDataCategories.setHasFixedSize(true)
        root.rcDataCategories.layoutManager = GridLayoutManager(activity, 3)

        root.btnAddCategory.setOnClickListener {
            replaceFragment(AddCategoryFragment())
        }


        root.txtSearchCategories.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(s)
            }
        })

        return root
    }


    override fun onClick(i: Int, type: Int) {
        when (type) {
            1 -> {

                val bundle = Bundle()
                bundle.putString("dataType", dataCategory[i].name)

                val f =
                    AdminProductsFragment()
                f.arguments = bundle
                replaceFragment(f)
            }
            2 -> {
                showDialogSetting(i)
            }
        }
    }


    private fun getAllCategory() {
        progressDialog.show()


        firebaseInstance.collection(
                "Categories"
            )
            .addSnapshotListener { querySnapshot,
                                   firebaseFirestoreException ->


                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                var i = 0
                dataCategory.removeAll(dataCategory)
                querySnapshot!!.forEach {

                    val category = it.toObject(Category::class.java)
                    if (category.uidUser == FirebaseAuth.getInstance().uid) {
                        dataCategory.add(category)
                    }
                    if (i == querySnapshot.size() - 1) {
                        progressDialog.dismiss()
                        adapter.notifyDataSetChanged()
                    }
                    i++
                }
                if (dataCategory.size == 0) {
                    progressDialog.dismiss()

                }
            }

    }


    private fun showDialogSetting(i: Int) {
        val bundle = Bundle()
        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.dailog_item)
        dialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        // Update
        dialog.btnUpdate.setOnClickListener {
            bundle.putParcelable("categoryUpdate", dataCategory[i])
            val f = AddCategoryFragment()
            f.arguments = bundle
            dialog.dismiss()
            replaceFragment(f)
        }

        dialog.btnDeleteD.setOnClickListener {
            showDialogDelete(i) {
                if (it) {
                    progressDialog.dismiss()
                    dialog.dismiss()
                    Snackbar.make(root, "Deletion successful", Snackbar.LENGTH_LONG).show()
                }
            }
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    private fun delete(
        nameCollection: String,
        idDoc: String,
        onComplete: (comp: Boolean) -> Unit
    ) {

        firebaseInstance.collection(nameCollection)
            .document(
                idDoc
            ).delete().addOnSuccessListener {
                onComplete(true)
            }

    }

    private fun showDialogDelete(
        i: Int,
        onComplete: (comp: Boolean) -> Unit
    ) {
        val deleteDialog = AlertDialog.Builder(activity!!)
        deleteDialog.setTitle("Delete Product ")
        deleteDialog.setMessage("Are you sure to delete?")
        deleteDialog.setCancelable(false)
        deleteDialog.setPositiveButton("Yes") { dialogInterface, j ->

            deleteImage(
                dataCategory[i].icon!!
            )
            delete("Categories", dataCategory[i].id!!) {
                if (it) {
                    adapter.notifyDataSetChanged()
                    onComplete(it)
                }
            }
            deleteAllProductWithCategory(dataCategory[i].name!!)
        }
        deleteDialog.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.dismiss()

        }
        deleteDialog.create().show()


    }


    private fun deleteAllProductWithCategory(category: String) {


        firebaseInstance.collection(
                "Products"
            )
            .addSnapshotListener { querySnapshot,
                                   firebaseFirestoreException ->

                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }

                querySnapshot!!.forEach {
                    val products = it.toObject(Product::class.java)

                    if (category == products.category &&
                        FirebaseAuth.getInstance().uid!! == products.uidUser
                    ) {
                        dataImageProduct.addAll(products.imageProduct)
                        for (image in dataImageProduct) {
                            deleteImage(
                                image
                            )
                        }
                        delete("Products", products.id) {
                            if (it) {
                                adapter.notifyDataSetChanged()
                                progressDialog.dismiss()
                            }
                        }
                    }
                }

                if (querySnapshot.isEmpty) {
                    dialog.dismiss()
                }
            }

    }

    /////
    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.container, f).addToBackStack("").commit()
    }

    override fun onResume() {
        super.onResume()
        getAllCategory()
    }

///////

    private fun deleteImage(
        idChi: String
    ) {
        progressDialog.setMessage("Deleting...!")
        progressDialog.show()
        storageInstance.reference
            .child(idChi)
            .delete().addOnSuccessListener {

            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Log.e("ttt", "$it")
            }
    }
/////////
}
