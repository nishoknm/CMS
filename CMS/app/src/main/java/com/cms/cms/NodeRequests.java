package com.cms.cms;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Nishok on 12/5/2015.
 */
public class NodeRequests {
    ProgressDialog progressDialog;
    public String callType;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://10.0.2.2:8090/users/";

    public NodeRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please wait...");
    }

    public NodeRequests(Context context, String callType) {
        this(context);
        this.callType = callType;
    }

    public void storeUserDataInBackground(User user,
                                          GetUserCallback userCallBack) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallBack).execute();
    }

    public void fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
        progressDialog.show();
        new fetchUserDataAsyncTask(user, userCallBack).execute();
    }

    public void fetchCollectionAsyncTask(GetCallback deptCallback) {
        progressDialog.show();
        new fetchCollectionAsyncTask(deptCallback).execute();
    }

    /**
     * parameter sent to task upon execution progress published during
     * background computation result of the background computation
     */

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Use HashMap, it works similar to NameValuePair
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("email", user.email);
            dataToSend.put("name", user.name);
            dataToSend.put("address", user.address);
            dataToSend.put("department", user.department);
            dataToSend.put("account", user.account);
            dataToSend.put("ssn", user.ssn);
            dataToSend.put("age", user.age + "");
            dataToSend.put("password", user.password);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            try {
                //Converting address String to URL
                URL url = new URL(SERVER_ADDRESS + "addUser");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                //Post Method
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                line = sb.toString();

                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("custom_check", line);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //Same return null, but if you want to return the read string (stored in line)
            //then change the parameters of AsyncTask and return that type, by converting
            //the string - to say JSON or user in your case
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            userCallBack.done(null);
        }

        private String getEncodedData(Map<String, String> data) {
            StringBuilder sb = new StringBuilder();
            for (String key : data.keySet()) {
                String value = null;
                try {
                    value = URLEncoder.encode(data.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (sb.length() > 0)
                    sb.append("&");
                sb.append(key + "=" + value);
            }
            return sb.toString();
        }

    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallBack;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("email", user.email);
            dataToSend.put("password", user.password);
            dataToSend.put("account", user.account);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            User returnedUser = null;

            try {
                //Converting address String to URL
                URL url = new URL(SERVER_ADDRESS + "getUser");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //Post Method
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(encodedStr);
                writer.flush();
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                String result = sb.toString();
                JSONObject jObject = new JSONObject(result);

                if (jObject.length() != 0) {
                    Log.v("happened", "2");
                    String name = jObject.getString("name");
                    String address = jObject.getString("address");
                    String department = jObject.getString("department");
                    String account = jObject.getString("account");
                    String ssn = jObject.getString("ssn");
                    int age = jObject.getInt("age");
                    returnedUser = new User(name, age, user.email,
                            user.password, address, department, ssn, account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            userCallBack.done(returnedUser);
        }

        private String getEncodedData(Map<String, String> data) {
            StringBuilder sb = new StringBuilder();
            for (String key : data.keySet()) {
                String value = null;
                try {
                    value = URLEncoder.encode(data.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (sb.length() > 0)
                    sb.append("&");

                sb.append(key + "=" + value);
            }
            return sb.toString();
        }
    }

    public class fetchCollectionAsyncTask extends AsyncTask<Void, Void, List> {
        GetCallback callback;
        ArrayList<String> collectionList = new ArrayList<String>();

        public fetchCollectionAsyncTask(GetCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List doInBackground(Void... params) {
            BufferedReader reader = null;
            String address = "getDepartments";
            String nameValue = "name";
            if(callType == "accounts"){
                address = "getAccounts";
                nameValue = "value";
            }
            try {
                //Converting address String to URL
                URL url = new URL(SERVER_ADDRESS + address);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                //Post Method
                con.setRequestMethod("POST");
                con.setDoOutput(true);
                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                JSONArray name = new JSONArray(sb.toString());
                for (int i = 0; i < name.length(); i++) {
                    collectionList.add(((JSONObject)name.get(i)).getString(nameValue));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return collectionList;
        }

        @Override
        protected void onPostExecute(List result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            callback.done(result);
        }
    }
}
