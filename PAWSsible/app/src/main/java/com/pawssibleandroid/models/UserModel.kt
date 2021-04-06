package com.pawssibleandroid.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
    @SerializedName("_id") val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val userType: String = "",
    val address: String = "",
) : Parcelable {

    override fun toString(): String {
        return "Name: $name\nEmail Address: $email\nPhone Number: $phone\nUser Type: ${
            if (userType.equals(
                    "O",
                    true
                )
            ) "Owner" else "Customer"
        }\nAddress: $address"
    }

}

/*
        "_id" : str(user['_id']),
        "name" : user['name'],
        "email": user['email'],
        "phone": user['phone'],
        "userType": user['userType'], o, c
        "address": user['address']
*/