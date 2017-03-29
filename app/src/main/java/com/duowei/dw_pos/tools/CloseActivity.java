package com.duowei.dw_pos.tools;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-29.
 */

public class CloseActivity {
    public static ArrayList<Activity>listActivity=new ArrayList();
    public static void addAcitity(Activity activity){
        listActivity.add(activity);
    }
    public static void finishActivity(){
        for(Activity a:listActivity){
            if(a!=null){
                a.finish();
            }
        }
    }
}
