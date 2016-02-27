package BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by rbech on 2/10/2016.
 */
public class MyBroadcastReceiver extends BroadcastReceiver{

    MyBroadcastListener listener;

    public MyBroadcastReceiver(MyBroadcastListener listener){
        this.listener= listener;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        listener.onReceive(context,intent);
    }
}
