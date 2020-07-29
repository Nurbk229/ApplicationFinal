package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.example.nurbk.ps.applicationfinal.Models.Users
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.dialog_checkout.*
import kotlinx.android.synthetic.main.fragment_product_user_cart.view.*


class CartUserProductFragment : Fragment() {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog

    private val dataU by lazy {
        ArrayList<Users>()
    }

    private val dataCa by lazy {
        ArrayList<Category>()
    }


    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }
    private val getProduct
        get() = firebaseInstance.collection(
                "Users"
            ).document(FirebaseAuth.getInstance().uid!!)
            .collection("Cart")

    val orderCo
        get() = firebaseInstance.collection("Users")
            .document(FirebaseAuth.getInstance().uid!!).collection("Oder's")
    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        activity!!.bottomBar.visibility = View.GONE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.txtNameFU.text = "My Cart"
        activity!!.toolbarU.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }

        root = inflater.inflate(R.layout.fragment_product_user_cart, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)

        getAllCategories()
        getAllUser()

        val product = arguments!!.getSerializable("product")!! as Product

        root.nameCartC.text = product.product
        root.priceCartC.text = "$${product.price}"
        root.txtCountCartC.text = product.count.toString()
        GlideApp.with(activity!!)
            .load(storageInstance.getReference(product.imageProduct[0]))
            .into(root.imageCartC)
        root.ratingCart.rating = product.rating

        getTotal(product)


        root.btnMCartC.setOnClickListener {
            if (product.count > 1) {
                product.count--
                getTotal(product)
            }
        }
        root.btnACartC.setOnClickListener {
            if (product.count < 10) {
                product.count++
                getTotal(product)
            }
        }

        root.btnCheckOutCartC.setOnClickListener {

            progressDialog.setMessage("Payment is in progress...")
            progressDialog.show()
            orderCo.document(product.id).set(product)

            for (user in dataU) {

                if (user.idUsers == product.uidUser &&
                    user.email.contains("@admin.com")
                ) {
                    user.price = (product.price * product.review) + user.price
                    firebaseInstance.collection(
                        "Users"
                    ).document(user.idUsers).set(user)
                }
            }
            for (cate in dataCa) {

                if (cate.uidUser == product.uidUser &&
                    product.category == cate.name
                ) {
                    cate.salary = (product.price * product.review) + cate.salary
                    firebaseInstance.collection(
                        "Categories"
                    ).document(cate.id!!).set(cate)
                }
            }
            getProduct.document(product.id).delete().addOnSuccessListener {
                progressDialog.dismiss()
                showDialogSuccess()

            }


        }

        return root

    }

    private fun getTotal(product: Product) {
        root.txtCountCartC.text = product.count.toString()
        root.quantityCartC.text = product.count.toString()
        root.priceTotalC.text = "$${(product.count * product.price.toInt())}"
        root.taxCartC.text = "$${(((product.count * product.price.toInt()) / 100) * 2)}"
        root.deliveryCartC.text = "$${(5 * (product.count / 2) / 2)}"
        root.totalCartC.text = "$" + ((((product.count * product.price.toInt()) / 100) * 2) +
                (5 * (product.count / 2) / 2)
                + (5 * (product.count / 2) / 2) +
                (product.count * product.price.toInt()))
    }


    private fun showDialogSuccess() {
        val dialog = Dialog(activity!!)
        dialog.setContentView(R.layout.dialog_checkout)
        dialog.setCancelable(false)
        dialog.buttonOk.setOnClickListener {
            dialog.dismiss()
            activity!!.onBackPressed()
        }
        dialog.show()

    }


    private fun getAllUser() {


        firebaseInstance.collection(
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


    private fun getAllCategories() {


        firebaseInstance.collection(
            "Categories"
        ).addSnapshotListener {
                querySnapshot,
                firebaseFirestoreException,
            ->

            if (firebaseFirestoreException != null) {
                return@addSnapshotListener
            }

            dataCa.removeAll(dataCa)
            querySnapshot!!.forEach {
                if (querySnapshot.isEmpty) {
                    return@addSnapshotListener
                }
                val cate = it.toObject(Category::class.java)
                dataCa.add(cate)
            }


        }

    }
}


