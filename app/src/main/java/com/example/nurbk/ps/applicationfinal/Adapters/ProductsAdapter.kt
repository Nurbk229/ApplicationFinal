package com.example.nurbk.ps.applicationfinal.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_deisgn_products.view.*

class ProductsAdapter(
    val activity: Activity,
    val mdata: ArrayList<Product>,
    var onClick: OnClickItemListener
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>(), Filterable {

    var data: ArrayList<Product> = mdata

    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(activity).inflate(R.layout.item_deisgn_products, parent, false)
        return ViewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.price.text = data[position].price.toString()
        holder.product.text = data[position].product

        GlideApp.with(activity)
            .load(storageInstance.getReference(data[position].imageProduct[0]))
            .into(holder.img)

        holder.btnItemProduct.setOnClickListener {
            onClick.onClickProduct(position, 1)
        }
        holder.btnItemProduct.setOnLongClickListener {
            onClick.onClickProduct(position, 2)
            true
        }

    }


    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val price = item.tvPrice
        val product = item.tvProduct
        val img = item.productImg
        val btnItemProduct = item.btnItemProduct

    }

    interface OnClickItemListener {
        fun onClickProduct(i: Int, type: Int)
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