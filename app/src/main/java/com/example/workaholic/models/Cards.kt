package com.example.workaholic.models

import android.os.Parcel
import android.os.Parcelable

data class Cards (
    val name : String = "",
    val createdBy : String = "",
    val assignedTo : ArrayList<String> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest){
        writeString(name)
        writeString(createdBy)
        writeStringList(assignedTo)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Cards> {
        override fun createFromParcel(parcel: Parcel): Cards {
            return Cards(parcel)
        }

        override fun newArray(size: Int): Array<Cards?> {
            return arrayOfNulls(size)
        }
    }
}