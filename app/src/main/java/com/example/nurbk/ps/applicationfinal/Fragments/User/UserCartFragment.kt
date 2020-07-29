package com.example.nurbk.ps.applicationfinal.Fragments.User

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nurbk.ps.applicationfinal.Adapters.CartAdapter
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.example.nurbk.ps.applicationfinal.Models.Users
import com.example.nurbk.ps.applicationfinal.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.dialog_checkout.*
import kotlinx.android.synthetic.main.fragment_cart_user.view.*


class UserCartFragment : Fragment(), CartAdapter.OnClickItemListener {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog

    private val adapter by lazy {
        CartAdapter(activity!!, data, this)
    }
    private val data by lazy {
        ArrayList<Product>()
    }
    private val dataP by lazy {
        ArrayList<Product>()
    }
    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }
    private val dataOrder by lazy {
        ArrayList<Product>()
    }

    private val dataU by lazy {
        ArrayList<Users>()
    }

    private val dataCa by lazy {
        ArrayList<Category>()
    }

    private var total = 0
    private var totalP = 0
    private var del = 0
    private var tex = 0.0
    private var qun = 0

    private var count = 1

    private val getProduct
        get() = firebaseInstance.collection(
                "Users"
            ).document(FirebaseAuth.getInstance().uid!!)
            .collection("Cart")

    private val currentUser
        get() = firebaseInstance.collection("Users")
            .document(FirebaseAuth.getInstance().uid!!).collection("Order")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        activity!!.toolbarU.visibility = View.VISIBLE

        activity!!.bottomBar.visibility = View.VISIBLE

        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(false)
        activity!!.txtNameFU.text = "My Cart"
        root = inflater.inflate(R.layout.fragment_cart_user, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)

        getAllOrder()
        getAllUser()
        getAllCategories()

        root.rcDataCart.layoutManager = LinearLayoutManager(activity!!)
        root.rcDataCart.adapter = adapter


        return root
    }

    override fun onResume() {
        super.onResume()
        getAllProducts()
        val orderCo = firebaseInstance.collection("Users")
            .document(FirebaseAuth.getInstance().uid!!).collection("Oder's")

        root.btnCheckOutCart.setOnClickListener {
            progressDialog.setMessage("Payment is in progress...")
            progressDialog.show()
            for ((i, prod) in data.withIndex()) {

                prod.review = prod.review + count


                firebaseInstance.collection(
                    "Products"
                ).document(prod.id).set(prod)

                for (user in dataU) {

                    if (user.idUsers == prod.uidUser &&
                        user.email.contains("@admin.com")
                    ) {
                        user.price = (prod.price * prod.review) + user.price
                        firebaseInstance.collection(
                            "Users"
                        ).document(user.idUsers).set(user)
                    }
                }
                for (cate in dataCa) {

                    if (cate.uidUser == prod.uidUser &&
                        prod.category == cate.name
                    ) {
                        cate.salary = (prod.price * prod.review) + cate.salary
                        firebaseInstance.collection(
                            "Categories"
                        ).document(cate.id!!).set(cate)
                    }
                }

                getProduct.document(prod.id).delete().addOnSuccessListener {
                    orderCo.document(prod.id).set(prod)
                    if (i == data.size - 1) {
                        progressDialog.dismiss()

                    }
                }

            }


            showDialogSuccess()
        }

    }


    private fun getAllOrder() {


        firebaseInstance.collection(
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


    override fun onClick(i: Int, type: Int, count: Int) {
        val product = data[i]
        when (type) {
            1 -> {
                val bundle = Bundle()
                bundle.putSerializable("product", product)
                val f = CartUserProductFragment()
                f.arguments = bundle
                replaceFragment(f)
            }
            2 -> {


                this.count = product.count++
                product.count = this.count
                getProduct.document(product.id).set(product)

                qun += 1
                totalP += product.price.toInt()
                tex += (((product.count * product.price.toInt()) / 100) * 2)
                del += (5 * (product.count / 2) / 2)
                total +=
                    ((((product.count * product.price.toInt()) / 100) * 2) +
                            (5 * (product.count / 2) / 2)
                            + (5 * (product.count / 2) / 2) +
                            (product.count * product.price.toInt()))
            }
            3 -> {

                this.count = product.count--
                product.count = this.count

                getProduct.document(product.id).set(product)

                qun -= 1
                totalP -= product.price.toInt()
                tex -= (((product.count * product.price.toInt()) / 100) * 2)
                del -= (5 * (product.count / 2) / 2)
                total -=
                    ((((product.count * product.price.toInt()) / 100) * 2) +
                            (5 * (product.count / 2) / 2)
                            + (5 * (product.count / 2) / 2) +
                            (product.count * product.price.toInt()))
            }
            4 -> {
                getProduct.document(product.id).delete()
                adapter.notifyDataSetChanged()
            }
        }

    }


    private fun getAllProductsCart() {


        firebaseInstance.collection(
                "Users"
            ).document(FirebaseAuth.getInstance().uid!!).collection("Cart")
            .addSnapshotListener {
                    querySnapshot,
                    firebaseFirestoreException,
                ->

                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                data.removeAll(data)
                var i = 0

                total = 0
                qun = 0
                totalP = 0
                tex = 0.0
                del = 0
                total = 0

                querySnapshot!!.forEach {
                    val product = it.toObject(Product::class.java)
                    for (prod in dataP) {
                        if (prod.id == product.id) {
                            data.add(product)
                            qun += product.count
                            totalP += (product.count * product.price.toInt())
                            tex += (((product.count * product.price.toInt()) / 100) * 2)
                            del += (5 * (product.count / 2) / 2)
                            total +=
                                ((((product.count * product.price.toInt()) / 100) * 2) +
                                        (5 * (product.count / 2) / 2)
                                        + (5 * (product.count / 2) / 2) +
                                        (product.count * product.price.toInt()))

                        }
                    }
                    if (i == querySnapshot.size() - 1) {
                        adapter.notifyDataSetChanged()
                        progressDialog.dismiss()
                        root.cardChe.visibility = View.VISIBLE
                        root.txtCartEmpty.visibility = View.GONE
                        root.priceTotal.text = "$${totalP}"
                        root.quantityCart.text = "$qun"
                        root.totalCart.text = "$${total}"
                        root.deliveryCart.text = "$$del"
                        root.taxCart.text = "$$tex"
                        root.totalCart.text = "$$total"
                    }
                    i++
                }
                if (data.size == 0) {
                    progressDialog.dismiss()
                    root.cardChe.visibility = View.GONE
                    root.txtCartEmpty.visibility = View.VISIBLE

                } else {

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
            dataP.removeAll(dataP)
            querySnapshot!!.forEach {
                val product = it.toObject(Product::class.java)
                dataP.add(product)
                if (i == querySnapshot.size() - 1) {
                    getAllProductsCart()
                }
                i++
            }
            if (dataP.size == 0) {
                progressDialog.dismiss()
            }

        }


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

    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerU, f)
            .addToBackStack("").commit()
    }

    private fun showDialogSuccess() {

        val dialog = Dialog(activity!!)

        dialog.setContentView(R.layout.dialog_checkout)


        dialog.setCancelable(false)

        dialog.buttonOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }


}


