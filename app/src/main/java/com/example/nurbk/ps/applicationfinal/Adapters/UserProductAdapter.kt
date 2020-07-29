package com.example.nurbk.ps.applicationfinal.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_design_product_user.view.*

class UserProductAdapter(
    val activity: Activity,
    val mdata: ArrayList<Product>,

    var onClick: OnClickItemListenerU
) : RecyclerView.Adapter<UserProductAdapter.ViewHolder>(), Filterable {

    var data: ArrayList<Product> = mdata

    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(activity).inflate(R.layout.item_design_product_user, parent, false)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.price.text = "$${data[position].price}"
        holder.product.text = data[position].product

        GlideApp.with(activity)
            .load(storageInstance.getReference(data[position].imageProduct[0]))
            .into(holder.img)

        holder.btnItemProduct.setOnClickListener {
            onClick.onClickProductU(position, 1)
        }
        holder.btnItemProduct.setOnLongClickListener {
            onClick.onClickProductU(position, 2)
            true
        }

    }


    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val price = item.txtPriceU
        val product = item.txtProductNU
        val img = item.productImgU
        val btnItemProduct = item.btnItemProductU

    }

    interface OnClickItemListenerU {
        fun onClickProductU(i: Int, type: Int)
    }

    override fun getFilter(): Filter {
        // Store The Resault from SearchView in Array and Refresh Data
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                data = results!!.values as ArrayList<Product>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charString: String = constraint.toString()
                data = if (charString.isEmpty())
                    mdata
                else {
                    val filteredList = ArrayList<Product>()
                    for (i in data) {
                        if (i.product.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(i)

                        }

                    }
                    filteredList

                }
                val filteredResult = FilterResults()
                filteredResult.values = data
                return filteredResult
            }


        }
    }

}