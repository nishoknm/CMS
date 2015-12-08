package com.cms.cms.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cms.cms.R;
import com.cms.cms.model.User;
import com.cms.cms.model.UserLocalStore;

public class CoursesFragment extends Fragment {
    UserLocalStore userlocalstore;
    User user;

    public CoursesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_courses, container, false);
        userlocalstore = new UserLocalStore(this.getActivity());
        user = userlocalstore.getLoggedInUser();
        TabLayout tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);

        if (user.account.equalsIgnoreCase("student")) {
            tabLayout.addTab(tabLayout.newTab().setText("ADD"));
            tabLayout.addTab(tabLayout.newTab().setText("REGISTERED"));
        } else if (user.account.equalsIgnoreCase("professor")) {
            tabLayout.addTab(tabLayout.newTab().setText("AVAILABLE"));
            tabLayout.addTab(tabLayout.newTab().setText("TEACHING"));
        }

        final ViewPager viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return inflatedView;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    AddCourseFragment tab1 = new AddCourseFragment();
                    return tab1;
                case 1:
                    CourseFragment tab2 = new CourseFragment();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
