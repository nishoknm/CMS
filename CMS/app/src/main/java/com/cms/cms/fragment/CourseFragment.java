package com.cms.cms.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cms.cms.R;
import com.cms.cms.adapter.CardAdapter;
import com.cms.cms.model.NodeRequests;
import com.cms.cms.model.UserLocalStore;
import com.cms.cms.model.callback.GetListCallback;

import org.json.JSONObject;

import java.util.ArrayList;

public class CourseFragment extends Fragment implements UpdateableFragment {

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    CardAdapter mAdapter;
    UserLocalStore userLocalStore;
    CoursesFragment.PagerAdapter pagerAdapter;

    public CourseFragment(CoursesFragment.PagerAdapter pagerAdapter) {
        // Required empty public constructor
        this.pagerAdapter = pagerAdapter;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_course, container, false);
        userLocalStore = new UserLocalStore(this.getActivity());

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        getCourses();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void getCourses() {
        NodeRequests serverRequest = new NodeRequests(this.getActivity());
        serverRequest.fetchUserCoursesDataAsyncTask(userLocalStore.getLoggedInUser(), new GetListCallback() {
            @Override
            public void done(ArrayList<JSONObject> items) {
                populateCourses(items);
            }
        });
    }

    private void populateCourses(ArrayList<JSONObject> courses) {
        mAdapter = new CardAdapter(courses, this.getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void update() {
        getCourses();
    }
}
