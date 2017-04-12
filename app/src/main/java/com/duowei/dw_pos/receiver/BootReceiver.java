package com.duowei.dw_pos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.duowei.dw_pos.LandActivity;

/**
 * Created by Administrator on 2017-04-12.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, LandActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(i);
    }
}
