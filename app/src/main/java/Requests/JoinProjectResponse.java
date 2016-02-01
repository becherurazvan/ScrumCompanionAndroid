package Requests;

/**
 * Created by rbech on 1/30/2016.
 */
public class JoinProjectResponse {
    boolean successful;
    String greeting;

    public JoinProjectResponse(boolean successful, String greeting) {
        this.successful = successful;
        this.greeting = greeting;
    }

    public JoinProjectResponse() {
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getGreeting() {
        return greeting;
    }
}
