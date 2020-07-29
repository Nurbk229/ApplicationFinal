package com.example.nurbk.ps.applicationfinal.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.R
import com.example.nurbk.ps.applicationfinal.Models.Product
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_desgin_cart.view.*

class CartAdapter(
    val activity: Activity, val data: ArrayList<Product>,
    var onClick: OnClickItemListener,
) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {


    override fun getItemCount(): Int {
        return data.size
    }

    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate =
            LayoutInflater.from(activity).inflate(R.layout.item_desgin_cart, parent, false)
        return MyViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text = data[position].product
        holder.price.text = """${"$"}${data[position].price.toInt() * data[position].count}"""
        holder.rating.rating = data[position].rating
        GlideApp.with(activity)
            .load(storageInstance.getReference(data[position].imageProduct[0]))
            .into(holder.image)
        holder.count.text = data[position].count.toString()

        holder.item.setOnClickListener {
            onClick.onClick(position, 1, data[position].count)
        }

        holder.btnA.setOnClickListener {
            holder.price.text = """${"$"}${data[position].price.toInt() * data[position].count}"""
            if (data[position].count < 10)
                onClick.onClick(position, 2, data[position].count++)
        }
        holder.btnM.setOnClickListener {
            holder.price.text = """${"$"}${data[position].price.toInt() * data[position].count}"""
            if (data[position].count > 1)
                onClick.onClick(position, 3, data[position].count--)
        }
        holder.delete.setOnClickListener {
            onClick.onClick(position, 4, 0)
        }
    }


    inner class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val name = item.nameCart
        val price = item.priceCart
        val rating = item.ratingCart
        val image = item.imageCart
        val btnM = item.btnMCart
        val btnA = item.btnACart
        val count = item.txtCountCart
        val delete = item.btnDeleteCart
        val item = item.btnMCartC
    }

    interface OnClickItemListener {
        fun onClick(i: Int, type: Int, count: Int)
    }


}