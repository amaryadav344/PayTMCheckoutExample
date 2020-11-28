package com.loopwiki.paytmcheckoutexample.Model;

public class UserDetails {

    private static User user;


    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        UserDetails.user = user;
    }
}
