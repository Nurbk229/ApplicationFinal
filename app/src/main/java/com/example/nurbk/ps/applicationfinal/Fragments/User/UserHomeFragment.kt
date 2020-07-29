package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nurbk.ps.applicationfinal.Adapters.UserProductAdapter
import com.example.nurbk.ps.applicationfinal.Adapters.SliderAdapter
import com.example.nurbk.ps.applicationfinal.Adapters.UserCategoryAdapter
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_user_home.view.*
import kotlinx.android.synthetic.main.fragment_user_home.view.tabLayoutHomeUser


class UserHomeFragment : Fragment(), UserCategoryAdapter.OnClickItemListener,
    UserProductAdapter.OnClickItemListenerU {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog


    private val adapterC by lazy {
        UserCategoryAdapter(activity!!, dataC, this)
    }

    private val dataC by lazy {
        ArrayList<Category>()
    }

    private val adapterS by lazy {
        SliderAdapter(activity!!, dataS)
    }

    private val dataS by lazy {
        ArrayList<String>()
    }

    private val adapterP by lazy {
        UserProductAdapter(activity!!, dataP, this)
    }
    private val adapterPB by lazy {
        UserProductAdapter(activity!!, dataPB, object : UserProductAdapter.OnClickItemListenerU {
            override fun onClickProductU(i: Int, type: Int) {
        bundle.putSerializable("product", dataPB[i])
        val f = UserProductDetailsFragment()
        f.arguments = bundle
        replaceFragment(f)
    }
    })
    }

    private val dataP by lazy {
        ArrayList<Product>()
    }
    private val dataPB by lazy {
        ArrayList<Product>()
    }

    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }
    private val bundle by lazy { Bundle() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        activity!!.bottomBar.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)
        activity!!.txtNameFU.text = "Home"
        activity!!.toolbarU.visibility = View.VISIBLE

        root = inflater.inflate(R.layout.fragment_user_home, container, false)

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)


        root.rcCategoryU.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        root.rcCategoryU.adapter = adapterC




        root.sliderPagerHomeUser.adapter = adapterS
        root.tabLayoutHomeUser.setupWithViewPager(root.sliderPagerHomeUser)


        root.rcProductU.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        root.rcProductU.adapter = adapterP

        root.rcProductBest.layoutManager =
            LinearLayoutManager(activity!!, LinearLayoutManager.HORIZONTAL, false)
        root.rcProductBest.adapter = adapterPB


        root.btnSeeAllLa.setOnClickListener {
            replaceFragment(UserSeeAllProductFragment())
        }

        root.btnSeeAllLa.setOnClickListener {


            replaceFragment(UserSeeAllProductFragment())
        }

        root.btnSeeAllBest.setOnClickListener {
            val f = UserSeeAllProductFragment()
            bundle.putString("Best", "Best")
            f.arguments = bundle
            replaceFragment(f)
        }

        root.btnSeeAllCU.setOnClickListener {
            replaceFragment(UserSeeAllCategoryFragment())
        }

        return root
    }

    override fun onClickCategory(i: Int) {


        bundle.putString("dataType", dataC[i].name)

        val f =
            UserSeeAllProductFragment()
        f.arguments = bundle
        replaceFragment(f)
    }

    override fun onClickProductU(i: Int, type: Int) {

        bundle.putSerializable("product", dataP[i])

        val f = UserProductDetailsFragment()
        f.arguments = bundle
        replaceFragment(f)
    }


    private fun getAllCategory() {

        progressDialog.show()
        firebaseInstance.collection(
            "Categories"
        ).addSnapshotListener {
                querySnapshot,
                firebaseFirestoreException,
            ->

            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }

            var i = 0
            dataC.removeAll(dataC)
            querySnapshot!!.forEach {
                val category = it.toObject(Category::class.java)
                if (i < 10)
                    dataC.add(category)
                if (i == querySnapshot.size() - 1) {
                    adapterC.notifyDataSetChanged()

                }
                i++
            }

        }


    }

    private fun getAllProducts() {


        firebaseInstance.collection(
            "Products"
        ).addSnapshotListener {
                querySnapshot,
                firebaseFirestoreException,
            ->

            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }

            var i = 0
            dataS.removeAll(dataS)
            dataP.removeAll(dataP)
            dataPB.removeAll(dataPB)
            querySnapshot!!.forEach {
                if (querySnapshot.isEmpty) {
                    return@addSnapshotListener
                }
                val product = it.toObject(Product::class.java)

                dataP.add(product)
                if (i < 5) {
                    Log.e("ttt", i.toString())
                    dataS.add(product.imageProduct[0])
                    adapterS.notifyDataSetChanged()
                }
                if (product.review >= 3) {
                    dataPB.add(product)
                    adapterPB.notifyDataSetChanged()
                }
                if (i == querySnapshot.size() - 1) {
                    adapterP.notifyDataSetChanged()
                    progressDialog.dismiss()
                }
                i++
            }
            if (dataP.size == 0) {
                progressDialog.dismiss()
            }

        }


    }


    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerU, f)
            .addToBackStack("").commit()
    }


    override fun onResume() {
        super.onResume()
        getAllCategory()

        getAllProducts()

    }

}