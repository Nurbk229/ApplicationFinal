package com.example.nurbk.ps.applicationfinal.Fragments.Admin


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
import com.example.nurbk.ps.applicationfinal.Adapters.ProductsAdapter
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dailog_item.*
import kotlinx.android.synthetic.main.fragment_admin_products.view.*
import kotlin.collections.ArrayList


class AdminProductsFragment : Fragment(), ProductsAdapter.OnClickItemListener {


    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog
    private lateinit var dialog: Dialog

    private val dataImageProduct by lazy {
        ArrayList<String>()
    }
    private val adapter by lazy {
        ProductsAdapter(activity!!, data, this)
    }

    private val data by lazy {
        ArrayList<Product>()
    }

    private val dataProduct by lazy {
        ArrayList<Product>()
    }
    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        activity!!.toolbar.visibility = View.VISIBLE
        activity!!.toolbar.title = "Products"
        activity!!.toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }


        root = inflater.inflate(R.layout.fragment_admin_products, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)



        root.btnAddProduct.setOnClickListener {

            activity!!.supportFragmentManager.beginTransaction()
                .replace(
                    R.id.container,
                    AdminAddProductFragment()
                ).addToBackStack("").commit()

        }
        root.txtSearchProducts.addTextChangedListener(object : TextWatcher {
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


    private fun getAllProduct() {
        progressDialog.show()


        firebaseInstance.collection(
                "Products"
            )
            .addSnapshotListener { querySnapshot,
                                   firebaseFirestoreException ->

                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }

                dataProduct.removeAll(dataProduct)
                data.removeAll(data)
                querySnapshot!!.forEach {

                    if (querySnapshot.size() == 0) {
                        return@addSnapshotListener
                    }
                    val products = it.toObject(Product::class.java)
                    if (products.uidUser == FirebaseAuth.getInstance().uid!!) {
                        dataProduct.add(products)
                    }


                }
                var i = 0
                if (arguments != null) {
                    val dataType = arguments!!
                    activity!!.toolbar.title = dataType.get("dataType").toString()

                    for (p in dataProduct) {

                        if (dataType.get("dataType") == p.category) {
                            data.add(p)
                        }
                        if (i == dataProduct.size - 1) {
                            progressDialog.dismiss()
                            adapter.notifyDataSetChanged()
                        }
                        i++
                    }
                    if (data.size == 0) {
                        progressDialog.dismiss()
                    }

                } else {
                    data.addAll(dataProduct)
                    progressDialog.dismiss()
                    adapter.notifyDataSetChanged()

                }


            }

    }

    override fun onResume() {
        super.onResume()
        getAllProduct()

        root.rcDataProducts.adapter = adapter
        root.rcDataProducts.setHasFixedSize(true)


        root.rcDataProducts.layoutManager =
            GridLayoutManager(activity, 3)


    }

    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.container, f).addToBackStack("").commit()
    }

    override fun onClickProduct(i: Int, type: Int) {
        when (type) {
            1 -> {
                val bundle = Bundle()
                bundle.putString("idProduct", dataProduct[i].id)
                val p =
                    ProductDetailsAdmin()
                p.arguments = bundle
                replaceFragment(p)
            }
            2 -> {
                showDialogSetting(i)
            }
        }
    }

    private fun updateProduct(i: Int) {
        val bundle = Bundle()
        bundle.putSerializable("productUpdate", data[i])
        val f =
            AdminAddProductFragment()
        f.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.container, f).addToBackStack("").commit()
    }

    private fun showDialogSetting(i: Int) {

        dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.dailog_item)
        dialog.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        // Update
        dialog.btnUpdate.setOnClickListener {
            dialog.dismiss()
            updateProduct(i)
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
            dataImageProduct.addAll(data[i].imageProduct)
            for (image in dataImageProduct) {
                deleteImage(
                    image
                )
            }
            delete("Products", data[i].id) {
                if (it) {
                    adapter.notifyDataSetChanged()
                    onComplete(it)
                }
            }
        }
        deleteDialog.setNegativeButton("No") { dialogInterface, i ->
            dialogInterface.dismiss()

        }
        deleteDialog.create().show()


    }


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

}
