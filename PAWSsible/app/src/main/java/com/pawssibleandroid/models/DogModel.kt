package com.pawssibleandroid.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DogModel(
    @SerializedName("_id") var dogId: String = "",
    @SerializedName("breedname") var breedName: String = "",
    var description: String = "",
    var active: Boolean = true,
    var allergies: String = "",
    var likes: String = "",
    var disLikes: String = "",
    var ageInMonths: Int = 0,
    var hourlyPrice: Double = 0.0,
    var ownerId: String = "",
    var photo: String = "",
    var ownerName: String = ""
) : Parcelable {
    override fun toString(): String {
        return "Breed Name: $breedName\nAge In Months: $ageInMonths\nHourly Price: $hourlyPrice\nDescription: $description\nLikes: $likes\n" +
                "DisLikes: $disLikes\n" +
                "Allergies: $allergies"
    }

}
/*
        "_id" : str(user['_id']),
       "breedname" : dog['breedname'],
        "description": dog['description'],
        "active": dog['active'],
        "allergies": dog['allergies'],
        "likes": dog['likes'],
        "disLikes": dog['disLikes'],
        "ageInMonths": dog['ageInMonths'],
        "hourlyPrice": dog['hourlyPrice'],
        "ownerId": dog['ownerId'],
        "photo": dog['photo'],
         "ownerName": name,
*/