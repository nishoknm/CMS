package com.cms.cms.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cms.cms.CMSLogin;
import com.cms.cms.R;
import com.cms.cms.model.User;
import com.cms.cms.model.UserLocalStore;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    UserLocalStore userLocalStore;
    EditText etName, etAge, etEmail, etAddress, etDepartment, etAcc, etSSN;
    Button bLogout;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        userLocalStore = new UserLocalStore(this.getActivity());

        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etName = (EditText) rootView.findViewById(R.id.etName);
        etAge = (EditText) rootView.findViewById(R.id.etAge);
        etAddress = (EditText) rootView.findViewById(R.id.etAddress);
        etDepartment = (EditText) rootView.findViewById(R.id.etdpt);
        etAcc = (EditText) rootView.findViewById(R.id.etAcc);
        etSSN = (EditText) rootView.findViewById(R.id.etSsn);
        bLogout = (Button) rootView.findViewById(R.id.bLogout);

        bLogout.setOnClickListener(this);
        displayUserDetails();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bLogout:
                userLocalStore.clearUserData();
                userLocalStore.setUserLoggedIn(false);
                Intent loginIntent = new Intent(this.getActivity(), CMSLogin.class);
                startActivity(loginIntent);
                break;
        }
    }

    private void displayUserDetails() {
        User user = userLocalStore.getLoggedInUser();
        if (user != null) {
            etEmail.setText(user.email);
            etName.setText(user.name);
            etAge.setText(user.age + "");
            etAddress.setText(user.address);
            etDepartment.setText(user.department);
            etAcc.setText(user.account);
            etSSN.setText(user.ssn);
        }
    }

}
