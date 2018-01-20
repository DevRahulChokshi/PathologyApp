package com.example.ebc003.pathologyapp;

/**
 * Created by Diksha on 25-04-2017.
 */
public class Config {

    //JSON URL
    public static final String DATA_URL1 = "http://www.ebusinesscanvas.com/pathalogy_lab/json1.php?user_email=";

    public static final String URL="http://www.ebusinesscanvas.com/pathalogy_lab/health_package_history.php";

    //Tags used in the JSON String
    public static final String TAG_PATIENT_INFO_ID = "patient_info_id";
    public static final String TAG_FIRSTNAME = "first_name";
    public static final String TAG_MIDDLENAME = "middle_name";
    public static final String TAG_LASTNAME= "last_name";
    public static final String TAG_AGE = "date_of_birth";
    public static final String TAG_CONTACTNUMBER = "contact_number";
    public static final String TAG_EMAIL = "e_mail";
    public static final String TAG_PINCODE = "pincode";
     //JSON array name
    public static final String JSON_ARRAY1 = "result";
    public static final String JSON_ARRAY_STATUS = "status";
    public static final String JSON_ARRAY_REGISTER_ID = "status";
    //URL to our login.php file
    public static final String LOGIN_URL = "http://www.ebusinesscanvas.com/pathalogy_lab/login.php";
     //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_EMAIL = "e_mail";
    public static final String KEY_PASSWORD = "password";
    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS = "success";
    public static final String TAG_ID = "id";
    public static final String TAG_COMP = "components";
    public static final String TAG_PRICE = "price";
    public static final String TAG_HEALTH = "name_of_health_pack";
    //Keys for Sharedpreferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "myloginapp";

    //This would be used to store the email of current logged in use
    // r
    public static final String EMAIL_SHARED_PREF = "email";
    public static final String ID_SHARED_PREF = "register_id";


    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    //Variables For WI-Fi
    public static final int TYPE_WIFI=1;
    public static final int TYPE_MOBILE=2;
    public static final int TYPE_NOT_CONNECTED=0;

}
