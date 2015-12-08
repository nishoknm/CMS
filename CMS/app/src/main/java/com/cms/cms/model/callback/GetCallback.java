package com.cms.cms.model.callback;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Nishok on 12/6/2015.
 */
public interface GetCallback {
    public abstract void done(HashMap<String, ArrayList> items);
}
