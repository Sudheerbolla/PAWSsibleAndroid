package com.pawssibleandroid.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookingModel(
    @SerializedName("_id") val bookingId: String = "",
    @SerializedName("dog") val dog: DogModel = DogModel(),
    @SerializedName("customer") val customer: UserModel = UserModel(),
    @SerializedName("owner") val owner: UserModel = UserModel(),
    val hours: Double = 0.0,
    val totalAmount: Double = 0.0,
    val timestamp: String = "",
    var status: String = "",
) : Parcelable
/*
 "_id" : str(booking['_id']),
        "dog" : dog,
        "owner": owner,
        "customer": customer,
        "hours": booking['hours'],
        "totalAmount": booking['total'],
        "timestamp":booking['timestamp'],
        "status":booking['status']

//         status - R - requested, X - cancelled, C - Completed, P - Accepted
*/