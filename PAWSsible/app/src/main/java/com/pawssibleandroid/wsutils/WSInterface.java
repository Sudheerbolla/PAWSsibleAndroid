package com.pawssibleandroid.wsutils;

import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * This Interface will have all the required APIS for app to work
 */
public interface WSInterface {

    @Headers("Content-Type: application/json")
    @POST("users/login")
    Call<JsonElement> performLogin(@Body RequestBody params);

    @Headers("Content-Type: application/json")
    @POST("users/register")
    Call<JsonElement> performRegistration(@Body RequestBody params);

    @Headers("Content-Type: application/json")
    @POST("users/updateuser")
    Call<JsonElement> performUserUpdate(@Body RequestBody params);

    @Headers("Content-Type: application/json")
    @POST("users/resetpassword")
    Call<JsonElement> resetPassword(@Body RequestBody params);

    @GET("dogs")
    Call<JsonElement> getAllDogsForCustomer();

    @GET("dogs/filter/{maxPrice}")
    Call<JsonElement> getAllDogsForCustomerFilter(@Path(value = "maxPrice", encoded = true) String maxPrice);

    @GET("dogs/{ownerId}")
    Call<JsonElement> getAllDogsForOwner(@Path(value = "ownerId", encoded = true) String ownerId);

    @GET("bookings/ownerbookings/{userId}")
    Call<JsonElement> getOwnerBookings(@Path(value = "userId", encoded = true) String userId);

    @GET("bookings/ownerbookingrequests/{userId}")
    Call<JsonElement> getOwnerBookingRequests(@Path(value = "userId", encoded = true) String userId);

    @GET("bookings/acceptbookingrequest/{requestId}")
    Call<JsonElement> acceptBookingRequest(@Path(value = "requestId", encoded = true) String requestId);

    @GET("bookings/rejectbookingrequest/{requestId}")
    Call<JsonElement> rejectBookingRequest(@Path(value = "requestId", encoded = true) String requestId);

    @GET("bookings/completebooking/{requestId}")
    Call<JsonElement> completeBooking(@Path(value = "requestId", encoded = true) String requestId);

    @Headers("Content-Type: application/json")
    @POST("bookings/createBooking")
    Call<JsonElement> createBooking(@Body RequestBody params);

    @Headers("Content-Type: application/json")
    @GET("bookings/customerbookings/{userId}")
    Call<JsonElement> getCustomerBookings(@Path(value = "userId", encoded = true) String userId);

    @Headers("Content-Type: application/json")
    @POST("dogs/updateDog")
    Call<JsonElement> updateDog(@Body RequestBody params);

    @Headers("Content-Type: application/json")
    @POST("dogs/addDog")
    Call<JsonElement> addDog(@Body RequestBody params);

    @Headers("Content-Type: application/json")
    @POST("dogs/location")
    Call<JsonElement> updateDogLocation(@Body RequestBody params);

    @GET("dogs/{dogId}")
    Call<JsonElement> getDogLocation(@Path(value = "dogId", encoded = true) String dogId);

}