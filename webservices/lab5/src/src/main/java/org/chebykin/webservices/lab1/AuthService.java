package org.chebykin.webservices.lab1;

import com.sun.jersey.core.util.Base64;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static Map<String, String> passwordStorage = new HashMap<String, String>() {{
        put("test", "test");
    }};
    public static boolean authenticate(String authCreds) {
        if (authCreds == null) return false;

        String usernameAndPassword = Base64.base64Decode(authCreds.replaceFirst("Basic ", ""));
        String[] authValue = usernameAndPassword.split(":");
        String login = authValue[0];
        String password = authValue[1];

        if ((login == null) || login.isEmpty()) return false;

        String correctPassword = passwordStorage.get(login);
        return ((correctPassword != null) && correctPassword.equals(password));
    }
}
