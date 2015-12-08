package com.cms.cms.model.callback;

import com.cms.cms.model.User;

/**
 * Created by Nishok on 12/5/2015.
 */
public interface GetUserCallback {

    public abstract void done(User returnedUser);
}
