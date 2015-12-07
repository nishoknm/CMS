package com.cms.cms;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CMSLogin extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button bLogin;
    TextView registerLink;
    EditText etEmail, etPassword;
    Spinner etAcc;

    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cmslogin);
        bLogin = (Button) findViewById(R.id.bLogin);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etAcc = (Spinner) findViewById(R.id.etAcc);
        etPassword = (EditText) findViewById(R.id.etPassword);
        registerLink = (TextView) findViewById(R.id.tvRegisterLink);

        bLogin.setOnClickListener(this);
        registerLink.setOnClickListener(this);
        etAcc.setOnItemSelectedListener(this);

        userLocalStore = new UserLocalStore(this);
        getAccounts();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLogin:
                String username = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String acc = etAcc.getSelectedItem().toString();

                User user = new User(username, password, acc);

                authenticate(user);
                break;
            case R.id.tvRegisterLink:
                Intent registerIntent = new Intent(CMSLogin.this, CMSRegister.class);
                startActivity(registerIntent);
                break;
        }
    }

    private void authenticate(User user) {
        NodeRequests serverRequest = new NodeRequests(this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CMSLogin.this);
        dialogBuilder.setMessage("Incorrect user details");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        userLocalStore.storeUserData(returnedUser);
        userLocalStore.setUserLoggedIn(true);
        startActivity(new Intent(this, CMSMain.class));
    }

    private void getAccounts() {
        NodeRequests serverRequest = new NodeRequests(this, "accounts");
        serverRequest.fetchCollectionAsyncTask(new GetCallback() {
            @Override
            public void done(List accounts) {
                populateAccounts(accounts);
            }
        });
    }

    private void populateAccounts(List accounts) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, accounts);
        etAcc.setAdapter(spinnerAdapter);
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
