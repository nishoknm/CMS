package com.cms.cms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cms.cms.model.NodeRequests;
import com.cms.cms.model.User;
import com.cms.cms.model.callback.GetCallback;
import com.cms.cms.model.callback.GetUserCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CMSRegister extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    EditText etName, etAge, etEmail, etPassword, etAddress, etSsn;
    Spinner etDept, etAcc;
    Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmsregister);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDept = (Spinner) findViewById(R.id.etdpt);
        etAcc = (Spinner) findViewById(R.id.etAcc);
        etSsn = (EditText) findViewById(R.id.etSsn);
        bRegister = (Button) findViewById(R.id.bRegister);

        etDept.setOnItemSelectedListener(this);
        etAcc.setOnItemSelectedListener(this);
        bRegister.setOnClickListener(this);

        getDepartments();
        getAccounts();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bRegister:
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String address = etAddress.getText().toString();
                String dept = etDept.getSelectedItem().toString();
                String acc = etAcc.getSelectedItem().toString();
                String ssn = etSsn.getText().toString();
                int age = Integer.parseInt(etAge.getText().toString());

                User user = new User(name, age, email, password, address, dept, ssn, acc);
                registerUser(user);
                break;
        }
    }

    private void getDepartments() {
        NodeRequests serverRequest = new NodeRequests(this, "departments");
        serverRequest.fetchCollectionAsyncTask(new GetCallback() {
            @Override
            public void done(HashMap<String, ArrayList> items) {
                populateDepartments(items.get("departments"));
            }
        });
    }

    private void populateDepartments(List departments) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, departments);
        etDept.setAdapter(spinnerAdapter);
    }

    private void getAccounts() {
        NodeRequests serverRequest = new NodeRequests(this, "accounts");
        serverRequest.fetchCollectionAsyncTask(new GetCallback() {
            public ArrayList AccList;

            @Override
            public void done(HashMap<String, ArrayList> accounts) {
                AccList = accounts.get("accounts");
                AccList.remove("Admin");
                populateAccounts(AccList);
            }
        });
    }

    private void populateAccounts(List accounts) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, accounts);
        etAcc.setAdapter(spinnerAdapter);
    }


    private void registerUser(User user) {
        NodeRequests serverRequest = new NodeRequests(this);
        serverRequest.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                Intent loginIntent = new Intent(CMSRegister.this, CMSLogin.class);
                startActivity(loginIntent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(
                getApplicationContext(),
                parent.getItemAtPosition(position).toString() + " Selected",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
