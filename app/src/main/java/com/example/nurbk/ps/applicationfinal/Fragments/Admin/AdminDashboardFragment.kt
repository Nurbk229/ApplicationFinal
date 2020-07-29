package com.example.nurbk.ps.applicationfinal.Fragments.Admin


import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_admin_dashboard.view.*


class AdminDashboardFragment : Fragment() {

    private lateinit var root: View
    private lateinit var progressDialog: ProgressDialog

    private val dataPie by lazy {
        ArrayList<DataEntry>()
    }

    private val firebaseInstance by lazy {
        FirebaseFirestore.getInstance()
    }
    private var total = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        ////////////////
        activity!!.toolbar.visibility = View.VISIBLE
        (activity as AppCompatActivity).supportActionBar!!
            .setDisplayHomeAsUpEnabled(true)
        activity!!.toolbar.title = "Dashboard"
        activity!!.toolbar.setNavigationOnClickListener {
            activity!!.onBackPressed()
        }
        //////////////

        root = inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Loading...!")
        progressDialog.setCancelable(false)
        progressDialog.show()
        getPieDashboard()

        return root
    }

    private fun getPieDashboard() {
        getAllCategory() {
            val pie = AnyChart.pie()

            pie.data(dataPie)
            pie.title("Fruits imported in 2015 (in kg)")
            pie.labels().position("outside")
            pie.legend().title().enabled(true)
            pie.legend().title()
                .text("Retail channels")
                .padding(0, 0, 10, 0)
            pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER)
            root.anyChart.setChart(pie)
            root.totalDashboard.text = "$${total.toString()}"
            progressDialog.dismiss()
            root.txtDashEmpty.visibility = View.GONE
            root.dashboard.visibility = View.VISIBLE
        }

    }

    private fun getAllCategory(onComplete: () -> Unit) {


        firebaseInstance.collection(
                "Categories"
            )
            .addSnapshotListener { querySnapshot,
                                   firebaseFirestoreException ->


                if (firebaseFirestoreException != null) {
                    return@addSnapshotListener
                }
                var i = 0
                querySnapshot!!.forEach {

                    val category = it.toObject(Category::class.java)
                    if (category.uidUser == FirebaseAuth.getInstance().uid) {
                        dataPie.add(ValueDataEntry(category.name, category.salary))
                        total += category.salary.toInt()
                    }
                    if (i == querySnapshot.size() - 1) {
                        progressDialog.dismiss()
                        onComplete()

                    }
                    i++
                }
                if (querySnapshot.isEmpty) {
                    progressDialog.dismiss()
                    return@addSnapshotListener
                }
            }


    }


}
