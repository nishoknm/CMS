package com.cms.cms;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nishok on 12/5/2015.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";

    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putString("name", user.name);
        userLocalDatabaseEditor.putString("email", user.email);
        userLocalDatabaseEditor.putString("password", user.password);
        userLocalDatabaseEditor.putString("address", user.address);
        userLocalDatabaseEditor.putString("department", user.department);
        userLocalDatabaseEditor.putString("account", user.account);
        userLocalDatabaseEditor.putString("ssn", user.ssn);
        userLocalDatabaseEditor.putInt("age", user.age);
        userLocalDatabaseEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.putBoolean("loggedIn", loggedIn);
        userLocalDatabaseEditor.commit();
    }

    public void clearUserData() {
        SharedPreferences.Editor userLocalDatabaseEditor = userLocalDatabase.edit();
        userLocalDatabaseEditor.clear();
        userLocalDatabaseEditor.commit();
    }

    public User getLoggedInUser() {
        if (userLocalDatabase.getBoolean("loggedIn", false) == false) {
            return null;
        }

        String name = userLocalDatabase.getString("name", "");
        String email = userLocalDatabase.getString("email", "");
        String password = userLocalDatabase.getString("password", "");
        String address = userLocalDatabase.getString("address", "");
        String dept = userLocalDatabase.getString("department", "");
        String acc = userLocalDatabase.getString("account", "");
        String ssn = userLocalDatabase.getString("ssn", "");
        int age = userLocalDatabase.getInt("age", -1);

        User user = new User(name, age, email, password, address, dept, ssn, acc);
        return user;
    }
}