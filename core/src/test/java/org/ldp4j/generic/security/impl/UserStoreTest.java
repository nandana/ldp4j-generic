package org.ldp4j.generic.security.impl;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UserStoreTest {

    @Test
    public void testDeserialize() throws Exception {

        Gson gson = new Gson();
        byte[] bstr =
                ByteStreams.toByteArray(this.getClass().getClassLoader().getResourceAsStream("user-config-test.json"));

        String userStoreJson = new String(bstr);

        System.out.println(userStoreJson);

        UserStore userStore = gson.fromJson(userStoreJson, UserStore.class);

        Map<String, User> users = userStore.getUsers();

        assertThat(users, is(notNullValue()));

        Set<String> userIds = users.keySet();

        assertThat(userIds, hasSize(3));
        assertThat(userIds, contains("admin", "test1", "test2"));

        User admin = users.get("admin");
        assertThat("username should be admin", admin.getUsername(), is("admin"));
        assertThat("password should be admin", admin.getPassword(), is("admin"));
        assertThat("role should be admin", admin.getRoles(), hasSize(1));


    }



}
