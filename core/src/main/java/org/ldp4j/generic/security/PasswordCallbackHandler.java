package org.ldp4j.generic.security;

import java.util.Map;

public interface PasswordCallbackHandler {

    public static final String USERNAME = "username";

    public static final String PASSWORD = "password";

    public void handle (Map<String, String> params);

}
