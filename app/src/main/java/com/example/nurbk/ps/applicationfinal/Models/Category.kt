package com.example.nurbk.ps.applicationfinal.Models

import android.os.Parcel
import android.os.Parcelable

data class Category(
    var id: String?,
    var uidUser: String?,
    var name: String?,
    var icon: String?,
    var salary: Double
) :
    Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble()
    )

    constructor() : this("", "", "","", 0.0)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(uidUser)
        parcel.writeString(name)
        parcel.writeString(icon)
        parcel.writeDouble(salary)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(parcel)
        }

        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}