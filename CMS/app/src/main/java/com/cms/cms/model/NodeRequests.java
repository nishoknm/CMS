package com.cms.cms.model;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cms.cms.model.callback.GetCallback;
import com.cms.cms.model.callback.GetListCallback;
import com.cms.cms.model.callback.GetModifyCallback;
import com.cms.cms.model.callback.GetUserCallback;

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
import java.util.Map;


/**
 * Created by Nishok on 12/5/2015.
 */
public class NodeRequests {
    ProgressDialog progressDialog;
    public String callType, email, courseAdd;
    public boolean course, isDelete;
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

    public NodeRequests(Context context, String email, String course, boolean isDelete) {
        this(context);
        this.email = email;
        this.courseAdd = course;
        this.isDelete = isDelete;
    }

    public NodeRequests(Context context, String email, String course) {
        this(context, email, course, false);
    }

    public NodeRequests(Context context, String callType, boolean course) {
        this(context, callType);
        this.course = course;
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

    public void fetchUserCoursesDataAsyncTask(User user, GetListCallback userCallBack) {
        progressDialog.show();
        new fetchUserCoursesDataAsyncTask(user, userCallBack).execute();
    }

    public void fetchCollectionAsyncTask(GetCallback deptCallback) {
        progressDialog.show();
        new fetchCollectionAsyncTask(deptCallback).execute();
    }

    public void updateCourseAsyncTask(GetModifyCallback callback) {
        progressDialog.show();
        new updateCourseAsyncTask(callback).execute();
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

    public class updateCourseAsyncTask extends AsyncTask<Void, Void, Void> {
        GetModifyCallback callBack;

        public updateCourseAsyncTask(GetModifyCallback callBack) {
            this.callBack = callBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Use HashMap, it works similar to NameValuePair
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("email", email);
            dataToSend.put("course", courseAdd);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            String address = "addCourse";
            if (isDelete) {
                address = "deleteCourse";
            }
            try {
                //Converting address String to URL
                URL url = new URL(SERVER_ADDRESS + address);
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
            callBack.done();
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

    public class fetchUserCoursesDataAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {
        User user;
        GetListCallback userCallBack;

        public fetchUserCoursesDataAsyncTask(User user, GetListCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("email", user.email);
            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            ArrayList<String> collectionList = new ArrayList<String>();

            try {
                //Converting address String to URL
                URL url = new URL(SERVER_ADDRESS + "getUserWithEmail");
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
                JSONArray courses = (JSONArray) jObject.get("courses");
                for (int i = 0; i < courses.length(); i++) {
                    collectionList.add(courses.getJSONObject(i).get("name").toString());
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
        protected void onPostExecute(ArrayList<String> courses) {
            super.onPostExecute(courses);
            progressDialog.dismiss();
            userCallBack.done(courses);
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

    public class fetchCollectionAsyncTask extends AsyncTask<Void, Void, HashMap> {
        GetCallback callback;
        ArrayList<String> collectionList = new ArrayList<String>();
        HashMap<String, ArrayList> deptCourseList = new HashMap<String, ArrayList>();

        public fetchCollectionAsyncTask(GetCallback callback) {
            this.callback = callback;
        }

        @Override
        protected HashMap doInBackground(Void... params) {
            BufferedReader reader = null;
            String address = "getDepartments";
            String nameValue = "name";
            if (callType == "accounts") {
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
                if (address.contains("Departments")) {
                    if (course) {
                        for (int i = 0; i < name.length(); i++) {
                            JSONObject dept = ((JSONObject) name.get(i));
                            collectionList.add(dept.getString(nameValue));
                            JSONObject courses = name.getJSONObject(i).getJSONObject("courses");
                            ArrayList courseList = new ArrayList<String>();
                            for (int j = 0; j < courses.length(); j++) {
                                courseList.add(courses.getJSONObject("c" + (j + 1)).get("name"));
                            }
                            deptCourseList.put(dept.getString(nameValue), courseList);
                        }
                    } else {
                        for (int i = 0; i < name.length(); i++) {
                            JSONObject names = ((JSONObject) name.get(i));
                            collectionList.add(names.getString(nameValue));
                            deptCourseList.put("departments", collectionList);
                        }
                    }

                } else {
                    for (int i = 0; i < name.length(); i++) {
                        JSONObject names = ((JSONObject) name.get(i));
                        collectionList.add(names.getString(nameValue));
                        deptCourseList.put("accounts", collectionList);
                    }
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
            return deptCourseList;
        }

        @Override
        protected void onPostExecute(HashMap result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            callback.done(result);
        }
    }
}
