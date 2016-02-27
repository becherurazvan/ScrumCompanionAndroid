package BroadcastReceivers;

import android.content.Context;
import android.content.Intent;

/**
 * Created by rbech on 2/10/2016.
 */
public interface MyBroadcastListener {
    public void onReceive(Context context, Intent intent);
}
