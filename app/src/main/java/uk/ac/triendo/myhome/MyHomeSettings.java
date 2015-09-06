package uk.ac.triendo.myhome;

import android.app.Application;

import org.springframework.http.HttpHeaders;

/**
 * Created by SHARC on 06/09/2015.
 */
public class MyHomeSettings extends Application {
    private String username;
    private String userID;
    private String userEmail;
    private HttpHeaders requestHeaders;

    public MyHomeSettings()
    {
        username = "";
        userID = "";
        userEmail = "";
        requestHeaders = null;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String password) {
        this.userID = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public HttpHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(HttpHeaders requestHeaders) {
        this.requestHeaders = requestHeaders;
    }
}
