package com.example.nurbk.ps.applicationfinal.Fragments.Admin

import android.app.ProgressDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.nurbk.ps.applicationfinal.Adapters.SliderAdapter
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_product_details_admin.view.*


class ProductDetailsAdmin : Fragment() {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog
    private lateinit var product: Product

    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }

    private val adapter by lazy {
        SliderAdapter(activity!!, data)
    }

    private val data by lazy {
        ArrayList<String>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity!!.toolbar.visibility = View.GONE

        root = inflater.inflate(R.layout.fragment_product_details_admin, container, false)

        root.btnBack.setOnClickListener {
            activity!!.onBackPressed()
        }
        setHasOptionsMenu(true)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)
        getAllProduct()

        root.slider_pager_details.adapter = adapter

        root.tabLayoutHomeAdmin.setupWithViewPager(root.slider_pager_details)

        root.btnEditProductDetais.setOnClickListener {
            updateProduct()
        }

        return root
    }

    private fun getAllProduct() {
        progressDialog.show()

        val productId = arguments!!
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
                    if (productId.getString("idProduct", "")
                        == products.id
                    ) {
                        root.txtPriceProductDetails.text = "$${products.price}"
                        root.txtNameProductDtails.text = products.product
                        root.txtDecsrptionProductDetails.text = products.description
                        root.ratingProductDetails.rating = products.rating
                        data.removeAll(data)
                        data.addAll(products.imageProduct)
                        adapter.notifyDataSetChanged()
                        progressDialog.dismiss()

                        product = products
                    }

                }


            }

    }

    private fun updateProduct() {
        val bundle = Bundle()
        bundle.putSerializable("productUpdate", product)
        val f =
            AdminAddProductFragment()
        f.arguments = bundle
        activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.container, f).addToBackStack("").commit()
    }

}
