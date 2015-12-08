package com.cms.cms.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cms.cms.R;

public class CourseDialogFragment extends DialogFragment {
    String name = null, details = null, time = null, professor = null;
    EditText etCourse, etDetails, etTime, etProf;
    Button bDismiss;

    public CourseDialogFragment(String name, String details, String time, String professor) {
        this.name = name;
        this.details = details;
        this.time = time;
        this.professor = professor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_course_details, container, false);

        getDialog().setTitle("Course Information");

        etCourse = (EditText) rootView.findViewById(R.id.etCourse);
        etDetails = (EditText) rootView.findViewById(R.id.etDetails);
        etTime = (EditText) rootView.findViewById(R.id.etTime);
        etProf = (EditText) rootView.findViewById(R.id.etProf);
        bDismiss = (Button) rootView.findViewById(R.id.bDismiss);

        etCourse.setText(this.name);
        etTime.setText(this.time);
        etDetails.setText(this.details);
        etProf.setText(this.professor);

        bDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseDialogFragment.this.dismiss();
                FragmentManager manager = CourseDialogFragment.this.getActivity().getSupportFragmentManager();
                Fragment frag = manager.findFragmentByTag("fragment_dialog");
                if (frag != null) {
                    manager.beginTransaction().remove(frag).commit();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //getDialog().getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        getDialog().getWindow().setLayout((6 * width) / 7, (4 * height) / 5);
    }

}
