package com.example.course_waiting_list;

/*
This assignment was HEAVILY based upon the SQLite tutorial linked in the project description.
However, while the structure is similar, the code performs according to the assignment specifications.
Tested on a Pixel 2 and works in both landscape and portrait mode. Comments are much more sparse
than previous assignments due to a time crunch with this and other assignments, but are still present.
 */

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.database.DatabaseHelper;
import com.example.database.EnrollmentRequest;
import com.example.utils.MyDividerItemDecoration;
import com.example.utils.RecyclerTouchListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private EnrollmentRequestsAdapter enrollmentRequestsAdapter;
    private final List<EnrollmentRequest> enrollmentRequestList = new ArrayList<>();
    private TextView noEnrollmentRequestsView;
    private DatabaseHelper db;

    /*
    enrollmentRequestsAdapter = adapter for recyclerview use.
    enrollmentRequestsList = list of enrollmentRequests to be displayed.
    noEnrollmentRequestsView = TextView displayed in case of no requests being present.
    db = database helper for SQL CRUD operations.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        noEnrollmentRequestsView = findViewById(R.id.empty_enrollment_requests_view);

        db = new DatabaseHelper(this);

        enrollmentRequestList.addAll(db.getAllEnrollmentRequests());

        //Setup for the FAB.
        FloatingActionButton fab1 = findViewById(R.id.fab1);
        fab1.setOnClickListener(view -> showEnrollmentRequestDialog(false, null, -1));

        //Setup for the recyclerview.
        enrollmentRequestsAdapter = new EnrollmentRequestsAdapter(enrollmentRequestList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(enrollmentRequestsAdapter);

        toggleEmptyEnrollmentRequests();

        //onItemTouchListener to allow the entries in the recyclerview to be selected.
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    //Method for creating an EnrollmentRequest.
    private void createEnrollmentRequest(String name, String course, int priority) {
        // Insertion of the request into the database, returning the id.
        long id = db.insertEnrollmentRequest(name, course, priority);

        // Getting the newly added request.
        EnrollmentRequest enrollmentRequest = db.getEnrollmentRequest(id);

        if (enrollmentRequest != null) {
            // Adding new EnrollmentRequest to the array list at 0 position.
            enrollmentRequestList.add(0, enrollmentRequest);

            /*
            One of the few things I dislike about this implementation is that when adding a new
            entry, it i not immediately sorted, but is sorted upon reopening the application. However,
            I was unsure of how to fix this issue.
             */

            // Refreshing the list
            enrollmentRequestsAdapter.notifyDataSetChanged();

            toggleEmptyEnrollmentRequests();
        }
    }

    //Method for updating an enrollmentRequest.
    private void updateEnrollmentRequest(String name, String course, String priority, int position) {
        EnrollmentRequest enrollmentRequest = enrollmentRequestList.get(position);
        //Updating enrollmentRequest information.
        enrollmentRequest.setName(name);
        enrollmentRequest.setCourse(course);
        enrollmentRequest.setPriority(priority);

        // Updating information in the database.
        db.updateEnrollmentRequest(enrollmentRequest);

        // Refresh the list
        enrollmentRequestList.set(position, enrollmentRequest);
        enrollmentRequestsAdapter.notifyItemChanged(position);

        toggleEmptyEnrollmentRequests();
    }

    //Method for deleting an EnrollmentRequest.
    private void deleteEnrollmentRequest(int position) {
        // Deleting the enrollmentRequest from database.
        db.deleteEnrollmentRequest(enrollmentRequestList.get(position));

        // Removing the enrollmentRequest from the list.
        enrollmentRequestList.remove(position);
        enrollmentRequestsAdapter.notifyItemRemoved(position);

        toggleEmptyEnrollmentRequests();
    }

    //Method for showing a dialog, called when an element is long clicked.
    private void showActionsDialog(final int position) {
        CharSequence[] colors = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        builder.setTitle("Choose an option");
        builder.setItems(colors, (dialog, which) -> {
            if (which == 0) {
                showEnrollmentRequestDialog(true, enrollmentRequestList.get(position), position);
            } else {
                deleteEnrollmentRequest(position);
            }
        });
        builder.show();
    }

    //Method for generating a dialog for creating or updating a note.
    private void showEnrollmentRequestDialog(final boolean shouldUpdate, final EnrollmentRequest enrollmentRequest, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.enrollmentrequest_dialog, null);

        //Creation of an AlertDialog.builder.
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this, R.style.DialogStyle);
        alertDialogBuilderUserInput.setView(view);

        //EditTexts within the dialog.
        final EditText inputName = view.findViewById(R.id.c_name_filter);
        final EditText inputCourse = view.findViewById(R.id.c_course);
        final EditText inputPriority = view.findViewById(R.id.c_priority);

        TextView dialogTitle = view.findViewById(R.id.dialog_title1);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.new_enrollment_request) : getString(R.string.edit_enrollment_request));

        //If an EnrollmentRequest is being updatyed, fill the EditTexts with that information.
        if (shouldUpdate && enrollmentRequest != null) {
            inputName.setText(enrollmentRequest.getName());
            inputCourse.setText(enrollmentRequest.getCourse());
            inputPriority.setText(enrollmentRequest.getPriority());
        }

        //Creation of the AlertDialog.
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", (dialogBox, id) -> {

                })
                .setNegativeButton("cancel",
                        (dialogBox, id) -> dialogBox.cancel());
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Show a toast message when no text is entered.
            if (TextUtils.isEmpty(inputName.getText().toString()) || TextUtils.isEmpty(inputCourse.getText().toString())
                    || TextUtils.isEmpty(inputPriority.getText().toString())) {
                Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                return;
            } //Show a toast when an invalid priority is entered.
            else if (Integer.parseInt(inputPriority.getText().toString()) > 5 ||
                    Integer.parseInt(inputPriority.getText().toString()) < 1) {
                Toast.makeText(MainActivity.this, "Priority must be between 1 and 5!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                alertDialog.dismiss();
            }

            // Check if user updating an EnrollmentRequest.
            if (shouldUpdate && enrollmentRequest != null) {
                // Update the EnrollmentRequest by its ID.
                updateEnrollmentRequest(inputName.getText().toString(), inputCourse.getText().toString(),
                        inputPriority.getText().toString(), position);
            } else {
                // Create a new EnrollmentRequest.
                createEnrollmentRequest(inputName.getText().toString(), inputCourse.getText().toString(),
                        Integer.parseInt(inputPriority.getText().toString()));
            }
        });
    }

    //Method to toggle the visibility of the no requests TextView.
    private void toggleEmptyEnrollmentRequests() {
        // check that the size of the list is greater than 0.

        if (db.getEnrollmentRequestsCount() > 0) {
            noEnrollmentRequestsView.setVisibility(View.GONE);
        } else {
            noEnrollmentRequestsView.setVisibility(View.VISIBLE);
        }
    }
}