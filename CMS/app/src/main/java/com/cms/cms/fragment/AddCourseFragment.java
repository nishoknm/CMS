package com.cms.cms.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.cms.cms.R;
import com.cms.cms.model.NodeRequests;
import com.cms.cms.model.User;
import com.cms.cms.model.UserLocalStore;
import com.cms.cms.model.callback.GetCallback;
import com.cms.cms.model.callback.GetModifyCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddCourseFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button bAddc;
    Spinner etDept, etCourse;
    UserLocalStore userLocalStore;

    public AddCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_addcourse, container, false);
        userLocalStore = new UserLocalStore(this.getActivity());

        bAddc = (Button) rootView.findViewById(R.id.bAddC);
        etDept = (Spinner) rootView.findViewById(R.id.etDept);
        etCourse = (Spinner) rootView.findViewById(R.id.etCourse);

        bAddc.setOnClickListener(this);
        etCourse.setOnItemSelectedListener(this);
        etDept.setOnItemSelectedListener(this);

        getDepartments();

        return rootView;
    }

    private void getDepartments() {
        NodeRequests serverRequest = new NodeRequests(this.getActivity(), "departments");
        serverRequest.fetchCollectionAsyncTask(new GetCallback() {
            @Override
            public void done(HashMap<String, ArrayList> items) {
                populateDepartments(items.get("departments"));
            }
        });
    }

    private void populateDepartments(List departments) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, departments);
        etDept.setAdapter(spinnerAdapter);
    }

    private void getCourses(final String dept) {
        NodeRequests serverRequest = new NodeRequests(this.getActivity(), "departments", true);
        serverRequest.fetchCollectionAsyncTask(new GetCallback() {
            @Override
            public void done(HashMap<String, ArrayList> items) {
                populateCourses(items.get(dept));
            }
        });
    }

    private void populateCourses(List dept) {

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_spinner_item, dept);
        etCourse.setAdapter(spinnerAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bAddC:
                String course = etCourse.getSelectedItem().toString();
                String courseDept = etDept.getSelectedItem().toString();
                addCourse(course, courseDept);
                break;
        }
    }

    private void addCourse(final String course, final String courseDept) {
        final User user = userLocalStore.getLoggedInUser();
        NodeRequests serverRequest = new NodeRequests(this.getActivity(), user.email, course, courseDept);
        serverRequest.updateCourseAsyncTask(new GetModifyCallback() {
            @Override
            public void done() {
                if (user.account.equalsIgnoreCase("professor")) {
                    NodeRequests serverRequest = new NodeRequests(AddCourseFragment.this.getActivity(), user.email, course, courseDept);
                    serverRequest.updateCourseDeptAsyncTask(new GetModifyCallback() {
                        @Override
                        public void done() {

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.etDept) {
            getCourses(parent.getItemAtPosition(position).toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
