package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nurbk.ps.applicationfinal.Adapters.UserProductAdapter
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_admin_products.view.*
import kotlinx.android.synthetic.main.fragment_user_see_all.view.*

/**
 * A simple [Fragment] subclass.
 */
class UserSeeAllProductFragment : Fragment(), UserProductAdapter.OnClickItemListenerU {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog

    private val adapterP by lazy {
        UserProductAdapter(activity!!, data, this)
    }

    private val data by lazy {
        ArrayList<Product>()
    }
    private val dataProduct by lazy {
        ArrayList<Product>()

    }
    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.bottomBar.visibility = View.GONE

        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.txtNameFU.text = "See All Products"
        activity!!.toolbarU.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        root = inflater.inflate(R.layout.fragment_user_see_all, container, false)

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)


        root.rcDataProductsU.layoutManager =
            GridLayoutManager(activity!!, 2)
        root.rcDataProductsU.adapter = adapterP


        root.txtSearchProductsU.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapterP.filter.filter(s)
            }
        })


        return root
    }


    override fun onResume() {
        super.onResume()

        getAllProducts()

    }

    private fun getAllProducts() {


        firebaseInstance.collection(
            "Products"
        ).addSnapshotListener { querySnapshot,
                                firebaseFirestoreException ->

            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }


            data.removeAll(data)
            querySnapshot!!.forEach {
                val product = it.toObject(Product::class.java)
                dataProduct.add(product)
            }
            var i = 0
            if (arguments != null) {

                val dataType = arguments!!
                if (dataType.getString("Best") == "Best") {
                    activity!!.txtNameFU.text = "Best Product"

                    for (p in dataProduct) {

                        if (p.review >= 3) {
                            data.add(p)
                            adapterP.notifyDataSetChanged()

                        }
                    }
                } else {
                    data.removeAll(data)
                    val dataType = arguments!!
                    for (p in dataProduct) {

                        if (dataType.get("dataType") == p.category) {
                            data.add(p)
                        }
                        if (i == dataProduct.size - 1) {
                            progressDialog.dismiss()
                            adapterP.notifyDataSetChanged()
                        }
                        i++
                    }
                    if (data.size == 0) {
                        progressDialog.dismiss()
                    }
                }
            } else {
                data.addAll(dataProduct)
                progressDialog.dismiss()
                adapterP.notifyDataSetChanged()

            }
        }
    }


    override fun onClickProductU(i: Int, type: Int) {
        val bundle = Bundle()
        bundle.putSerializable("product", data[i])
        val f = UserProductDetailsFragment()
        f.arguments = bundle
        replaceFragment(f)
    }

    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerU, f)
            .addToBackStack("").commit()
    }
}
