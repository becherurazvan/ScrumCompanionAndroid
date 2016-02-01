package Requests;

/**
 * Created by rbech on 1/30/2016.
 */
public class JoinProjectRequest {
    String userEmail;
    String invitationCode;

    public JoinProjectRequest(String userEmail, String invitationCode) {
        this.userEmail = userEmail;
        this.invitationCode = invitationCode;
    }

    public JoinProjectRequest() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getInvitationCode() {
        return invitationCode;
    }
}
