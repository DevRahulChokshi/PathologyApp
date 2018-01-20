package com.example.ebc003.pathologyapp;

/**
 * Created by admin-2 on 5/18/2017.
 */
public class Subjects {

    String SubjectName;
    String Id;
    String desc;

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


}
