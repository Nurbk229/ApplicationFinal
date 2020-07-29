package com.example.nurbk.ps.applicationfinal.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.google.firebase.storage.FirebaseStorage
import android.widget.Filter
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.R
import kotlinx.android.synthetic.main.item_design_categories.view.*


class CategoriesAdapter(
    val activity: Activity, val mdata: ArrayList<Category>,
    var onClick: OnClickItemListener
) :
    RecyclerView.Adapter<CategoriesAdapter.MyViewHolder>(), Filterable {

    var data: ArrayList<Category> = mdata

    override fun getItemCount(): Int {
        return data.size
    }

    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val category = item.tvCategoryName
        val img = item.iconImg
        val btnItemAllCategory = item.btnItemAllCategory
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate =
            LayoutInflater.from(activity).inflate(R.layout.item_design_categories, parent, false)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.category.text = data[position].name
        GlideApp.with(activity)
            .load(storageInstance.getReference(data[position].icon!!))
            .into(holder.img)

        holder.btnItemAllCategory.setOnClickListener {
            onClick.onClick(position, 1)
        }
        holder.btnItemAllCategory.setOnLongClickListener {
            onClick.onClick(position, 2)
            true
        }
    }

    interface OnClickItemListener {
        fun onClick(i: Int, type: Int)
    }

    override fun getFilter(): Filter {
        // Store The Resault from SearchView in Array and Refresh Data
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                data = results!!.values as ArrayList<Category>
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charString: String = constraint.toString()
                data = if (charString.isEmpty())
                    mdata
                else {
                    val filteredList = ArrayList<Category>()
                    for (i in data) {
                        if (i.name!!.toLowerCase().contains(charString.toLowerCase())) {
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