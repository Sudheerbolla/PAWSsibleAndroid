package com.pawssibleandroid.wsutils;

/**
 * All constants required for webservice
 */
public class WSUtils {

    public static final String TAG = "PAWSsible";
    //SECONDS
    public static final long CONNECTION_TIMEOUT = 125;

    public static final String BASE_DEV_URL = "http://3.239.158.7:5000/";
    public static final String BASE_PROD_URL = "http://3.239.158.7:5000/";
    public static final String BASE_URL = BASE_DEV_URL;

    // This is for caching web service resposnes properly
    public static final int REQ_FOR_LOGIN = 100;
    public static final int REQ_FOR_USER_REGISTRATION = 101;
    public static final int REQ_FOR_GET_ALL_DOGS = 102;
    public static final int REQ_FOR_GET_BOOKINGS = 103;
    public static final int REQ_FOR_ADD_DOG = 105;
    public static final int REQ_FOR_EDIT_DOG = 106;
    public static final int REQ_FOR_UPDATE_USER = 107;
    public static final int REQ_FOR_RESET_PASSWORD = 108;
    public static final int REQ_FOR_UPDATE_BOOKING_REQUESTS = 109;
    public static final int REQ_FOR_CREATE_BOOKING = 110;
    public static final int REQ_FOR_ADD_LOCATION = 111;
    public static final int REQ_FOR_GET_DOG_LOCATIONS = 112;

}