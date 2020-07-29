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
import com.example.nurbk.ps.applicationfinal.Models.Category
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_user_category.view.*

class UserCategoryAdapter(
    val activity: Activity,
    val mdata: ArrayList<Category>,
    var onClick: OnClickItemListener
) :
    RecyclerView.Adapter<UserCategoryAdapter.ViewHolder>(), Filterable {
    var data: ArrayList<Category> = mdata
    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(activity).inflate(R.layout.item_user_category, parent, false)
        return ViewHolder(
            inflate
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.category.text = data[position].name
        GlideApp.with(activity)
            .load(storageInstance.getReference(data[position].icon!!))
            .into(holder.img)
        holder.btnItemCategory.setOnClickListener {
            onClick.onClickCategory(position)
        }

    }


    class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val category = item.txtCategoryU
        val img = item.categoryImgU
        val btnItemCategory = item.btnItemCategoryU

    }

    interface OnClickItemListener {
        fun onClickCategory(i: Int)
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