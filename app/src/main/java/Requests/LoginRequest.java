package Requests;

/**
 * Created by rbech on 1/25/2016.
 */
public class LoginRequest {
       public  String email;
        public String authCode;
        public String idToken;

    public LoginRequest(String email, String authCode, String idToken) {
        this.email = email;
        this.authCode = authCode;
        this.idToken = idToken;
    }
}
