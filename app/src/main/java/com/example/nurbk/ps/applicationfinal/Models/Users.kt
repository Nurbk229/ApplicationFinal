package com.example.nurbk.ps.applicationfinal.Models

data class Users(
    var idUsers: String,
    var name: String,
    var email: String,
    var phone: String,
    var description: String,
    var password: String,
    var price: Int,
    var profileImage: String,


) {

    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        "",
        0,
        ""
    )
}