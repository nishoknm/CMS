package com.cms.cms.model.callback;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Nishok on 12/7/2015.
 */
public interface GetListCallback {
    public abstract void done(ArrayList<JSONObject> items);
}
