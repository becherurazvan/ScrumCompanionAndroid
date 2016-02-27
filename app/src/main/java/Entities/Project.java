package Entities;

import java.util.ArrayList;
import java.util.Random;

import Scrum.ProductBacklog;


public class Project {
    ArrayList<String> members;  // email and GCMtoken
    String id;
    String title;
    String leaderEmail;
    String invitationCode;


    String projectFolderId;
    String userStoriesFileId;
    String notesFileId;

    ProductBacklog productBacklog;

    public Project(String leaderEmail, String title) {
        members = new ArrayList<>();
        this.leaderEmail = leaderEmail;
        this.title = title;
        id =  String.valueOf(Math.abs(new Random().nextInt(9000)+1000));
        invitationCode = id;
        productBacklog = new ProductBacklog(id,title);


    }

    public Project(){

    }



    public ProductBacklog getProductBacklog(){
        return productBacklog;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getId() {
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

    public void setUserStoriesFileId(String userStoriesFileId) {
        this.userStoriesFileId = userStoriesFileId;
    }

    public void setNotesFileId(String notesFileId) {
        this.notesFileId = notesFileId;
    }

    public void setProjectFolderId(String projectFolderId) {
        this.projectFolderId = projectFolderId;
    }


}
