package com.example.ebc003.pathologyapp;

/**
 * Created by Juned on 1/20/2017.
 */

public class Subjects1 {

    String SubjectName;
    String Id;
    String desc;
    String day,day2;
    String time,time1;
    String pathology_lab_name;
    String address;
    public String getName() {

        return SubjectName;
    }

    public void setName(String TempName) {

        this.SubjectName = TempName;
    }
    public String getId() {

        return Id;
    }

    public void setId(String Id1) {

        this.Id = Id1;
    }


    public String getDesc() {

        return desc;
    }

    public void setDesc(String desc1) {

        this.desc = desc1;
    }
    public String getDay() {

        return day;
    }

    public void setDay(String day1) {

        this.day = day1;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time1) {

        this.time= time1;
    }

    public void setPathName(String pathology_lab_name) {
        this.pathology_lab_name = pathology_lab_name;
    }

    public String getPathName() {
        return pathology_lab_name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
