package Requests;

/**
 * Created by rbech on 2/10/2016.
 */
public class LoginRequest extends Request {
    String gcmToken;
    public LoginRequest(String tokenId,String gcmToken) {
        super(tokenId);
    }

    public LoginRequest() {
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }
}
