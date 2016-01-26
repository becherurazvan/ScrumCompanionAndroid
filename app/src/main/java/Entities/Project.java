package Entities;

import java.util.ArrayList;

/**
 * Created by rbech on 1/26/2016.
 */
public class Project {


    ArrayList<String> invitedUsers;
    int id;
    String title;
    String leaderEmail;
    String invitationCode;

    String projectFolderId;

    public ArrayList<String> getInvitedUsers() {
        return invitedUsers;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLeaderEmail() {
        return leaderEmail;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public String getProjectFolderId() {
        return projectFolderId;
    }
}
