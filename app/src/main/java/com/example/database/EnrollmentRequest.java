package com.example.database;

public class EnrollmentRequest {
    public static final String TABLE_NAME = "Enrollment_Requests";

    public static final String ID = "id";
    public static final String NAME = "Student_Name";
    public static final String COURSE = "Course_ID";
    public static final String TIMESTAMP = "Request_Date";
    public static final String PRIORITY = "Student_Priority";

    /* SQL Columns:
    ID = autoincremented unique ID.
    NAME = student name (both first and last).
    COURSE = course name that the student is registered for.
    TIMESTAMP = Generated timestamp at the time of registration.
    PRIORITY = Priority level of the student, with freshman being 1, up to graduate being 5.
     */

    private int id;
    private String course, name, timestamp, priority;

    /*
    Variables corresponding to the respective SQL columns.

    NOTE REGARDING PRIORITY BEING A STRING:
    Priority was originally an int, but this caused an error when attempting to display it in
    the recyclerview (instead of getting/setting the text as that integer, android studio would
    instead search for a string resource of the id 0x[priority], or a hex number corresponding to it.
    Storing this as a String, while confusing, fixes this issue entirely.
     */

    //SQL Table
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NAME + " TEXT NOT NULL,"
            + COURSE + " TEXT NOT NULL,"
            + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + PRIORITY + " INT NOT NULL"
            + ")";

    public EnrollmentRequest() {
    }

    //Constructor
    public EnrollmentRequest(int id, String name, String course, String timestamp, String priority)
    {
        this.id = id;
        this.name = name;
        this.course = course;
        this.timestamp = timestamp;
        this.priority = priority;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
