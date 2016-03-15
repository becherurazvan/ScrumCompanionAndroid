package Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import Database.Record;
import Scrum.ProductBacklog;
import Scrum.Sprint;


public class Project {
    ArrayList<String> members;  // email and GCMtoken
    String id;
    String title;
    ArrayList<User> users;
    String leaderEmail;
    String invitationCode;
    String projectFolderId;
    String userStoriesFileId;
    ProductBacklog productBacklog;
    Record record;

    public Project(String leaderEmail, String title) {
        members = new ArrayList<>();
        this.leaderEmail = leaderEmail;
        this.title = title;
        id = String.valueOf(Math.abs(new Random().nextInt(9000) + 1000));
        invitationCode = id;
        productBacklog = new ProductBacklog(id,this);
        record = new Record();
    }

    public Project() {
    }

    public Record getRecord(){
        return record;
    }

    public ProductBacklog getProductBacklog() {
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


    public void setProjectFolderId(String projectFolderId) {
        this.projectFolderId = projectFolderId;
    }

    public String getProjectFolderId() {
        return projectFolderId;
    }

    public String getUserStoriesFileId() {
        return userStoriesFileId;
    }

    @JsonIgnore
    public int calculateDaysInProject(){
        Date firstSprintStartDate=null;
        Date lastSprintEndDate=null;
        int latestSprint=0;

        for(Sprint sprint:productBacklog.getSprints()){
            if(sprint.getNumber()>latestSprint) {
                latestSprint = sprint.getNumber();
                if(firstSprintStartDate==null){
                    firstSprintStartDate = sprint.getStartDateCalendar().getTime();
                }
            }
        }

        for(Sprint s:productBacklog.getSprints()){
            if(s.getNumber()==latestSprint)
                lastSprintEndDate = s.getEndDateCalendar().getTime();
        }

        if(firstSprintStartDate==null){
            return 0;
        }

        long diff = lastSprintEndDate.getTime() - firstSprintStartDate.getTime();

        int difference = (int) TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);

        return difference;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
