package Entities;


import android.graphics.Bitmap;

public class User {
    public String email;
    public String name;
    public String pictureUrl;
    private String userId;
    private String currentProject;
    private String accessToken;
    private String refreshToken;
    private String gcmToken;


    public User(String email, String name, String pictureUrl, String userId) {
        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.userId = userId;

    }



    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }


}
