package jiangjia.recorder_j.FileManager;

import android.content.ContentValues;
import android.content.Context;

import jiangjia.recorder_j.Utils.DButils;

/**
 * Created by KelceiJ on 2017/3/8.
 */

public class MyRunnable implements Runnable {

    private float seconds;
    private String filePath;
    private Context context;

    public MyRunnable(float seconds, Context context, String filePath) {
        this.seconds = seconds;
        this.context = context;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        DButils dButils = new DButils(context);
        ContentValues values = new ContentValues();
        values.put("AudioPath", filePath);
        values.put("AudioDuration",seconds);
        dButils.insert(values);
    }
}
