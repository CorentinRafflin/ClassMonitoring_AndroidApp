package com.easynote.objects;

import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Meeting {

    //id of the meeting
    private int id;

    // name of the meeting
    private String name;

    // Date of the Meeting
    private Date date;


    //Date of end of the meeting
    private Date endingDate;

    //Type of the meeting
    private int type_id;

    private int project_id;


    public Meeting(int project_id, int type_id, String name, Date date, Date endingDate) {
        this.project_id = project_id;
        this.type_id=type_id;
        this.name = name;
        this.date = date;
        this.endingDate = endingDate;
    }

    public Meeting(int id, int project_id, int type_id, String name, Date date, Date endingDate) {
        this.id = id;
        this.project_id = project_id;
        this.type_id=type_id;
        this.name = name;
        this.date = date;
        this.endingDate = endingDate;
    }



    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Date getEndingDate() {
        return endingDate;
    }
    

    public int getTypeId() {
        return type_id;
    }

    public int getProjectId() {
        return project_id;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTypeId(int type_id) {
        this.type_id = type_id;
    }



    public void setEndingDate(Date endingDate){
        this.endingDate = endingDate;
    }



}
