package com.example.course_waiting_list;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.database.EnrollmentRequest;


public class EnrollmentRequestsAdapter extends RecyclerView.Adapter<EnrollmentRequestsAdapter.MyViewHolder> {

    private final List<EnrollmentRequest> enrollmentRequestsList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView dot;
        public TextView timestamp;
        public TextView priority;
        public TextView course;

        /*XML elements corresponding to those in enrollmentrequest_list_row.xml
        name = student name.
        dot = dot marking each entry.
        timestamp = time of registration/
        priority = student priority level.
        course = course the student is registered for.
         */

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
            priority = view.findViewById(R.id.priority);
            course = view.findViewById(R.id.course);
        }
    }


    public EnrollmentRequestsAdapter(List<EnrollmentRequest> enrollmentRequestsList) {
        this.enrollmentRequestsList = enrollmentRequestsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.enrollmentrequest_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        EnrollmentRequest enrollmentRequest = enrollmentRequestsList.get(position);

        holder.name.setText(enrollmentRequest.getName());
        holder.course.setText(enrollmentRequest.getCourse());

        String priority = enrollmentRequest.getPriority();

        //switch statement to display the priority as the student's current year, as well as number.
        switch (priority) {
            case "1": holder.priority.setText(String.format("%s - Freshman", priority));
                      break;
            case "2": holder.priority.setText(String.format("%s - Sophomore", priority));
                      break;
            case "3": holder.priority.setText(String.format("%s - Junior", priority));
                      break;
            case "4": holder.priority.setText(String.format("%s - Senior", priority));
                      break;
            case "5": holder.priority.setText(String.format("%s - Graduate", priority));
                      break;
        }

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(enrollmentRequest.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return enrollmentRequestsList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            assert date != null;
            return fmtOut.format(date);
        } catch (ParseException ignored) {

        }

        return "";
    }
}
