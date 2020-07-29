package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nurbk.ps.applicationfinal.Activities.MapsActivity
import com.example.nurbk.ps.applicationfinal.Adapters.SliderAdapter
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.example.nurbk.ps.applicationfinal.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.dailog_rating.*
import kotlinx.android.synthetic.main.fragment_product_details_user.view.*


class UserProductDetailsFragment : Fragment() {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog


    private val adapter by lazy {
        SliderAdapter(activity!!, data)
    }

    private val data by lazy {
        ArrayList<String>()
    }
    private val dataOrder by lazy {
        ArrayList<Product>()
    }
    private val dataP by lazy {
        ArrayList<Product>()
    }
    private val mFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUser
        get() = mFirestore.collection("Users")
            .document(FirebaseAuth.getInstance().uid!!).collection("Cart")

    private var isShow = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.bottomBar.visibility = View.GONE
        activity!!.toolbarU.visibility = View.GONE


        root = inflater.inflate(R.layout.fragment_product_details_user, container, false)

        root.btnBackU.setOnClickListener {
            activity!!.onBackPressed()
        }

        getAllProducts()
        getAllOrder()
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)


        val bundle = arguments!!
        val product = bundle.getSerializable("product")!! as Product


        activity!!.txtNameFU.text = product.product
        data.removeAll(data)
        data.addAll(product.imageProduct)


        root.slider_pager_detailsU.adapter = adapter
        root.tabLayoutHomeAdminU.setupWithViewPager(root.slider_pager_detailsU)

        root.txtNameProductDtailsU.text = product.product
        root.txtPriceProductDetailsU.text = "$${product.price}"
        root.ratingProductDetailsU.rating = product.rating
        root.txtDecsrptionProductDetailsU.text = product.description


        root.btnAddProductDetaisU.setOnClickListener {


            for (od in dataOrder) {
                if (od.id == product.id) {
                    isShow = false
                }
            }
            if (isShow)
                for (prod in dataP) {
                    if (prod.id == product.id) {
                        isShow = false
                    }
                }
            if (isShow)
                showDialogRating(product)

            addCart(product)
            Snackbar.make(root, "  Added successfully", Snackbar.LENGTH_LONG).show()

        }

        root.btnLocationProduct.setOnClickListener {
            val intentMap = Intent(activity!!, MapsActivity::class.java)
            intentMap.putExtra("location", product.location)
            startActivity(intentMap)

        }
        return root
    }

    private fun addCart(product: Product) {
        for ((i, prod) in dataP.withIndex()) {
            if (prod.id == product.id) {

                product.count = prod.count++
                currentUser.document(product.id).set(product).addOnSuccessListener {


                }

                mFirestore.collection(
                        "Products"
                    )
                    .document(product.id).set(product)

                break

            } else if (i == dataP.size - 1) {
                product.count = 1
                currentUser.document(product.id).set(product).addOnSuccessListener {


                }
            }
        }
        if (dataP.size == 0)
            currentUser.document(product.id).set(product).addOnSuccessListener {

            }
        product.countSela = product.countSela + 1
        mFirestore.collection(
                "Products"
            )
            .document(product.id).set(product).addOnSuccessListener {
                Log.e("ttt", "true")

            }
    }


    private fun getAllOrder() {


        mFirestore.collection(
                "Users"
            ).document(FirebaseAuth.getInstance().uid!!).collection("Oder's")
            .addSnapshotListener {
                    querySnapshot,
                    firebaseFirestoreException,
                ->

                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }

                dataOrder.removeAll(dataP)
                querySnapshot!!.forEach {
                    val product = it.toObject(Product::class.java)
                    dataOrder.add(product)

                }

            }
    }


    private fun getAllProducts() {


        mFirestore.collection(
                "Users"
            ).document(FirebaseAuth.getInstance().uid!!).collection("Cart")
            .addSnapshotListener {
                    querySnapshot,
                    firebaseFirestoreException,
                ->

                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }

                var i = 0
                dataP.removeAll(dataP)
                querySnapshot!!.forEach {
                    val product = it.toObject(Product::class.java)
                    dataP.add(product)
                    if (i == querySnapshot.size() - 1) {
                        adapter.notifyDataSetChanged()
                        progressDialog.dismiss()
                    }
                    i++
                }
                if (dataP.size == 0) {
                    progressDialog.dismiss()

                }

            }
    }

    private fun showDialogRating(product: Product) {
        val dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.dailog_rating)
        dialog.setCancelable(false)
        var ratingP = 0f

        dialog.ratingProduct.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingP = rating

            product.rating =
                (rating + (product.rating * (product.countRating - 1))) / (product.countRating + 1)

        }

        dialog.btnOkRating.setOnClickListener {
            product.countRating = product.countRating + 1
            if (ratingP == 0f) {
                Toast.makeText(activity!!, "Please rate", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            mFirestore.collection(
                    "Products"
                )
                .document(product.id).set(product).addOnSuccessListener {

                }
            dialog.dismiss()
        }


        dialog.show()


    }

}
