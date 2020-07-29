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
import com.example.nurbk.ps.applicationfinal.Adapters.UserCategoryAdapter
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_user_see_all.view.*
import kotlinx.android.synthetic.main.fragment_user_see_all_category.view.*


class UserSeeAllCategoryFragment : Fragment(), UserCategoryAdapter.OnClickItemListener {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog

    private val adapter by lazy {
        UserCategoryAdapter(activity!!, data, this)
    }

    private val data by lazy {
        ArrayList<Category>()
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

        activity!!.toolbarU.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        activity!!.txtNameFU.text = "All Categories"
        root = inflater.inflate(R.layout.fragment_user_see_all_category, container, false)

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.show()


        getAllCategory()


        root.rcDataSeeAllCategory.layoutManager = GridLayoutManager(activity!!, 5)
        root.rcDataSeeAllCategory.adapter = adapter

        root.txtSearchCategoriesU.addTextChangedListener(object : TextWatcher {
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


    override fun onClickCategory(i: Int) {

        val bundle = Bundle()
        bundle.putString("dataType", data[i].name)

        val f =
            UserSeeAllProductFragment()
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
            data.removeAll(data)
            querySnapshot!!.forEach {
                val category = it.toObject(Category::class.java)
                data.add(category)
                if (i == querySnapshot.size() - 1) {
                    adapter.notifyDataSetChanged()
                    progressDialog.dismiss()
                }
                i++


            }

        }
    }


    private fun replaceFragment(f: Fragment) {
        activity!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.containerU, f)
            .addToBackStack("").commit()
    }
}
