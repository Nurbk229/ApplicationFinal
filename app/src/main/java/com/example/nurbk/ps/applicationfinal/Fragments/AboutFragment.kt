package com.example.nurbk.ps.applicationfinal.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nurbk.ps.applicationfinal.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.android.synthetic.main.fragment_about.view.*


class AboutFragment : Fragment() {

    private val share
        get() =
            activity!!.getSharedPreferences("File", Context.MODE_PRIVATE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /////
        if (share.getString("Reg", "")!! == "@admin.com") {
            activity!!.toolbar.visibility = View.VISIBLE
            (activity as AppCompatActivity).supportActionBar!!
                .setDisplayHomeAsUpEnabled(true)
            activity!!.toolbar.title = "About App"
            activity!!.toolbar.setNavigationOnClickListener {
                activity!!.onBackPressed()
            }
        } else {
            activity!!.toolbarU.visibility = View.VISIBLE
            (activity as AppCompatActivity).supportActionBar!!
                .setDisplayHomeAsUpEnabled(true)
            activity!!.txtNameFU.text = "About App"
            activity!!.toolbarU.setNavigationOnClickListener {
                activity!!.onBackPressed()
            }
        }
        ////////

        val root = inflater.inflate(R.layout.fragment_about, container, false)

        //For Move to Us Instgram Accounts
        root.l1.movementMethod = LinkMovementMethod.getInstance()
        root.l2.movementMethod = LinkMovementMethod.getInstance()

        // For Share App Button
        root.btnShare.setOnClickListener {
            val i = Intent(Intent.ACTION_SEND)
            i.type = "text/plain"
            val shareBody = "https://www.mediafire.com/file/lqbagnxrbzi55va/Shopping.apk/file"
            val shareSub = R.string.description.toString()
            i.putExtra(Intent.EXTRA_SUBJECT, shareSub)
            i.putExtra(Intent.EXTRA_TEXT, shareBody)
            activity!!.startActivity(Intent.createChooser(i, "Share Useing"))
        }

        return root
    }


}
