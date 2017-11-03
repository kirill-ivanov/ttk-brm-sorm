package com.ttk.brm.sorm.core;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class User {
    private static Map<String, User> USER_POOL = new HashMap();

    private String login;
    private String password;
    private String sessionId;

    public static User findBySessionId(String sessionId) {
        if (USER_POOL.get(sessionId) != null) {
            return USER_POOL.get(sessionId);
        }
        return null;
    }

    public static boolean setUserInPool(String sessionId, User user) {
        if (sessionId != null && user != null) {
            User.USER_POOL.put(sessionId, user);
            return true;
        } else {
            return false;
        }
    }

    public static void removeUserInPool(String sessionId) {
        User.USER_POOL.remove(sessionId);
    }

    public static User findByLoginAndPassword(String login, String password) {
        if ("test".equals(login) && User.generateSignSHA256HEX("test").equals(password)) {
            User user = new User();
            user.login = login;
            user.password = password;

            //Поиск в базе
            //TODO

            return user;
        } else {
            return null;
        }
    }


    public static String generateRndCode() {
        String code = "";
        for (int i = 0; i < 5; i++) {
            code += (int) (Math.random() * 10);
        }
        return code;
    }

    public static String generateSignSHA256HEX(String baseString) {
        String key = "e4b9618f-3400-4c40-bbfc-f8c469d73fca";
        String result = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
            mac.init(secret);
            byte[] digest = mac.doFinal(baseString.getBytes());
            result = new String(Hex.encodeHex(digest));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
