package Database;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;

import Entities.Project;
import Scrum.DateManager;
import Scrum.Resource;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by rbech on 3/14/2016.
 */
public class Action implements Comparable<Action> {


    Calendar date;
    String memberEmail;
    String memberName;
    String message;
    int id;


    public Action(String memberEmail,String memberName,  String message) {
        this.memberEmail = memberEmail;
        this.memberName=  memberName;
        this.message = message;
        date = Calendar.getInstance();

    }

    public Action() {
    }

    public void setId(int id){
        this.id = id;
    }
    public Calendar getDate() {
        return date;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Action o) {
        return o.getDate().compareTo(getDate());

    }

    public SpannableString generateSpannableText(OnResourceClickedListener listener){

        SpannableString spannableString = new SpannableString(Resource.parseFinalText(message));

        ArrayList<Resource> resources = Resource.generateResourcesFromString(message);

        for(Resource r:resources){
            spannableString.setSpan(new ForegroundColorSpan(Color.GREEN),r.getStartPos(),r.getEndPos(),0);
            spannableString.setSpan(new RelativeSizeSpan(1.1f),r.getStartPos(),r.getEndPos(),0);

            ClickableSpan clickableSpan =new MyClickableSpan(r,listener);

            spannableString.setSpan(clickableSpan,r.getStartPos(),r.getEndPos(),0);

        }

        return spannableString;
    }

    public Resource getMainResource(){
        if(Resource.generateResourcesFromString(message).size()<1)
            return null;
        return  Resource.generateResourcesFromString(message).get(0);
    }


    public class MyClickableSpan extends ClickableSpan{

        public Resource resource;
        OnResourceClickedListener listener;

        public MyClickableSpan(Resource resource,OnResourceClickedListener listener){
            this.resource =resource;
            this.listener = listener;
        }

        @Override
        public void onClick(View widget) {
            listener.onResourceClicked(resource);
        }
    }

    public interface OnResourceClickedListener{
        public void onResourceClicked(Resource resource);
    }
}
