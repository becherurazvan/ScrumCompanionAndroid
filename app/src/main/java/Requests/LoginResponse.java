package Requests;


public class LoginResponse {
    public String greeting;
    public boolean loginSuccesful;
    public boolean partOfTeam;
    public boolean gotInvites;



    public String getGreeting() {
        return greeting;
    }

    public boolean isLoginSuccesful() {
        return loginSuccesful;
    }

    public boolean isPartOfTeam() {
        return partOfTeam;
    }

    public boolean isGotInvites() {
        return gotInvites;
    }
}
