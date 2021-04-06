package com.pawssibleandroid.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
    @SerializedName("_id") val locationId: String = "",
    val dogId: String = "",
    val customerId: String = "",
    val lat: String = "",
    val long: String = "",
    val timestamp: String = "",
) : Parcelable
/*
  "_id" : str(location['_id']),
        "dogId" : location['dogId'],
        "customerId": location['customerId'],
        "lat": location['lat'],
        "long": location['long'],
        "timestamp": location['timestamp']
*/