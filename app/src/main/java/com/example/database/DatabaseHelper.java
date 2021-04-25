package com.example.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Database Name
    private static final String DATABASE_NAME = "Registration Waiting List";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Table Creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EnrollmentRequest.CREATE_TABLE);
    }

    //Upgrading database (drops tables on upgrade)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + EnrollmentRequest.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    //Function to add an EnrollmentRequest
    public long insertEnrollmentRequest(String name, String course, int priority) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(EnrollmentRequest.NAME, name);
        values.put(EnrollmentRequest.COURSE, course);
        values.put(EnrollmentRequest.PRIORITY, priority);

        long id  = db.insert(EnrollmentRequest.TABLE_NAME, null, values);

        db.close();

        return id;
    }

    //Delete an EnrollmentRequest.
    public void deleteEnrollmentRequest(EnrollmentRequest request) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EnrollmentRequest.TABLE_NAME, EnrollmentRequest.ID + " = ?",
                new String[]{String.valueOf(request.getId())});
        db.close();
    }

    //Update and EnrollmentRequest.
    public int updateEnrollmentRequest(EnrollmentRequest request) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EnrollmentRequest.NAME, request.getName());
        values.put(EnrollmentRequest.COURSE, request.getCourse());
        values.put(EnrollmentRequest.PRIORITY, request.getPriority());

        return db.update(EnrollmentRequest.TABLE_NAME, values, EnrollmentRequest.ID + " = ?",
                new String[]{String.valueOf(request.getId())});
    }

    //Query the database for an EnrollmentRequest.
    public EnrollmentRequest getEnrollmentRequest(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = db.query(EnrollmentRequest.TABLE_NAME,
                new String[]{EnrollmentRequest.ID, EnrollmentRequest.NAME, EnrollmentRequest.COURSE,
                        EnrollmentRequest.TIMESTAMP, EnrollmentRequest.PRIORITY},
                EnrollmentRequest.ID + "=?",
                new String[]{String.valueOf(id)},null, null, null, null);

        if (cur != null)
            cur.moveToFirst();

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(
                cur.getInt(cur.getColumnIndex(EnrollmentRequest.ID)),
                cur.getString(cur.getColumnIndex(EnrollmentRequest.NAME)),
                cur.getString(cur.getColumnIndex(EnrollmentRequest.COURSE)),
                cur.getString(cur.getColumnIndex(EnrollmentRequest.TIMESTAMP)),
                cur.getString(cur.getColumnIndex(EnrollmentRequest.PRIORITY)));

        cur.close();

        return enrollmentRequest;
    }

    //Get all EnrollmentRequests.
    public List<EnrollmentRequest> getAllEnrollmentRequests() {
        List<EnrollmentRequest> enrollmentRequests = new ArrayList<>();

        //I chose to have mine ordered by course, then priority level in descending order, with timestamp being secondary.
        String selectQuery = "SELECT * FROM "+EnrollmentRequest.TABLE_NAME+
                " ORDER BY " + EnrollmentRequest.COURSE + " DESC, " +
                EnrollmentRequest.PRIORITY + " DESC, " + EnrollmentRequest.TIMESTAMP + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);

        if (cur.moveToFirst()) {
            do {
                EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
                enrollmentRequest.setId(cur.getInt((cur.getColumnIndex(EnrollmentRequest.ID))));
                enrollmentRequest.setName(cur.getString(cur.getColumnIndex((EnrollmentRequest.NAME))));
                enrollmentRequest.setCourse(cur.getString(cur.getColumnIndex(EnrollmentRequest.COURSE)));
                enrollmentRequest.setTimestamp(cur.getString(cur.getColumnIndex(EnrollmentRequest.TIMESTAMP)));
                enrollmentRequest.setPriority(cur.getString(cur.getColumnIndex(EnrollmentRequest.PRIORITY)));

                enrollmentRequests.add(enrollmentRequest);
            } while (cur.moveToNext());
        }

        cur.close();
        db.close();

        return enrollmentRequests;
    }

    //Get a count of enrollment requests in the database.
    public int getEnrollmentRequestsCount() {
        String countQuery = "SELECT  * FROM " + EnrollmentRequest.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(countQuery, null);

        int count = cur.getCount();
        cur.close();

        return count;
    }
}
