package org.ldp4j.generic.security.impl;

import java.util.List;
import java.util.Map;

public class UserStore {

    private Map<String, User> users;

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }
}
