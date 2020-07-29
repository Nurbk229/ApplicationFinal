package com.example.nurbk.ps.applicationfinal.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.example.nurbk.ps.applicationfinal.Glids.GlideApp
import com.example.nurbk.ps.applicationfinal.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.slider_item.view.*

class SliderAdapter(var activity: Activity, var data: ArrayList<String>) : PagerAdapter() {

    private val storageInstance by lazy {
        FirebaseStorage.getInstance()
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val root = LayoutInflater.from(activity).inflate(R.layout.slider_item, container, false)


            GlideApp.with(activity)
                .load(storageInstance.getReference(data[position]))
                .into(root.slider_img)

        container.addView(root)
        return root

    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return data.size

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}