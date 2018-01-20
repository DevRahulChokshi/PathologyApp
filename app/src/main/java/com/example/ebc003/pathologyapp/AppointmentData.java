package com.example.ebc003.pathologyapp;

import android.util.Log;

/**
 * Created by EBC003 on 11/20/2017.
 */

public  class AppointmentData {

    private String TAG=AppointmentData.class.getSimpleName ();


    private int id;
    private String userName;
    private String test_type_Name;
    private String schedule;
    private String time;


    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getUserName () {
        return userName;
    }

    public void setUserName (String userName) {
        this.userName = userName;
        Log.i (TAG,"MODEL DATA:-"+userName);
    }

    public String getTest_type_Name () {
        return test_type_Name;
    }

    public void setTest_type_Name (String test_type_Name) {
        this.test_type_Name = test_type_Name;
        Log.i (TAG,"MODEL DATA:-"+test_type_Name);

    }

    public String getSchedule () {
        return schedule;
    }

    public void setSchedule (String schedule) {
        this.schedule = schedule;
        Log.i (TAG,"MODEL DATA:-"+schedule);

    }

    public String getTime () {
        return time;
    }

    public void setTime (String time) {
        this.time = time;
        Log.i (TAG,"MODEL DATA:-"+time);
    }

}
