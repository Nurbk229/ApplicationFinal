package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nurbk.ps.applicationfinal.Adapters.UserProductAdapter
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_user_oder.view.*
import kotlinx.android.synthetic.main.fragment_user_see_all.view.*



class UserOderFragment : Fragment() ,UserProductAdapter.OnClickItemListenerU{

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog



    private val mFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val adapter by lazy {
        UserProductAdapter(activity!!, data, this)
    }
    private val data by lazy {
        ArrayList<Product>()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity!!.toolbarU.visibility = View.VISIBLE

        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.txtNameFU.text = "My Order"
        activity!!.toolbarU.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        root = inflater.inflate(R.layout.fragment_user_oder, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)
        getAllProducts()
        root.rcDataProductsUO.layoutManager =
            GridLayoutManager(activity!!, 2)
        root.rcDataProductsUO.adapter = adapter

        root.txtSearchProductsUO.addTextChangedListener(object : TextWatcher {
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


    private fun getAllProducts() {


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

                var i = 0
                data.removeAll(data)
                querySnapshot!!.forEach {
                    val product = it.toObject(Product::class.java)
                    data.add(product)
                    if (i == querySnapshot.size() - 1) {
                        adapter.notifyDataSetChanged()
                        progressDialog.dismiss()
                    }
                    i++
                }
                if (data.size == 0) {
                    progressDialog.dismiss()

                }

            }
    }


    override fun onClickProductU(i: Int, type: Int) {

    }
}


