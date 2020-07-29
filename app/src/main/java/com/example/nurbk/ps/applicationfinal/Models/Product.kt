package com.example.nurbk.ps.applicationfinal.Models

import java.io.Serializable

data class Product(
    var id: String,
    var uidUser: String,
    var price: Int,
    var product: String,
    var description: String,
    var rating: Float,
    var location: String,
    var category: String,
    var imageProduct: ArrayList<String>,
    var count: Int,
    var review: Int,
    var countSela: Int,
    var countRating: Int
) : Serializable {


    constructor() : this(
        "",
        "",
        0,
        "",
        "",
        0f,
        "",
        "",
        arrayListOf(""),
        1,
        0,
        0,
        1
    )


}