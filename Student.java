package com.studenthub.model;

public class Student {
    private int id;
    private String name, email, course, phone;
    private int yearLevel;

    public Student() {}

    public Student(int id, String name, String email, String course, int yearLevel, String phone) {
        this.id=id; this.name=name; this.email=email; this.course=course;
        this.yearLevel=yearLevel; this.phone=phone;
    }

    public Student(String name, String email, String course, int yearLevel, String phone) {
        this(0,name,email,course,yearLevel,phone);
    }

    public int getId(){return id;}
    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getCourse(){return course;}
    public int getYearLevel(){return yearLevel;}
    public String getPhone(){return phone;}
    public void setId(int id){this.id=id;}
}
