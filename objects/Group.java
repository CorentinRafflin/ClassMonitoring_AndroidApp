package com.easynote.objects;

import java.util.ArrayList;

public class Group {

    // Number of the group
    private int groupNumber;

    // Id of the group
    private int id;

    // Id of the project
    private int projectId;

    //List of the students, who belong to the group
    private ArrayList<Student> students;



    //Constructors

    public Group(int id, int groupNumber) {
        this.id = id;
        this.groupNumber = groupNumber;
    }

    public Group(int groupId, int groupNumber, int projectId) {
        this.id = groupId;
        this.groupNumber = groupNumber;
        this.projectId = projectId;
    }

    // Getters
    public int getGroupNumber() {
        return groupNumber;
    }

    public String getName() { return "Groupe " + groupNumber; }
    public int getId() {
        return id;
    }

    public int getProjectId() {
        return projectId;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    // Setters
    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


}
