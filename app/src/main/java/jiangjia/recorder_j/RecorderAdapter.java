package jiangjia.recorder_j;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jiangjia.recorder_j.FileManager.AudioEntity;

/**
 * Created by jiangjia on 17/3/3.
 */

public class RecorderAdapter extends ArrayAdapter<AudioEntity> {
    private List<AudioEntity> mDatas;
    private Context mContext;
    private int mMinItemWidth;
    private int mMaxItemWidth;

    private LayoutInflater mInflater;

    public RecorderAdapter(Context context, List<AudioEntity> objects) {
        super(context, -1, objects);
        mContext=context;
        mDatas=objects;

        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mMaxItemWidth=(int)(outMetrics.widthPixels*0.7f);
        mMinItemWidth=(int)(outMetrics.widthPixels*0.15f);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView=mInflater.inflate(R.layout.item_recorder, parent, false);
            holder=new ViewHolder();
            holder.seconds= (TextView) convertView.findViewById(R.id.id_recorder_time);
            holder.length=convertView.findViewById(R.id.id_recorder_length);

            convertView.setTag(holder);
        }
        else{
            holder=(ViewHolder)convertView.getTag();
        }

        //设置时间宽度
        holder.seconds.setText(Math.round(getItem(position).AudioDuration)+"\"");//求距离某数最近的整数（可能比某数大，也可能比它小）,返回int型或者long型（上一个函数返回double型）
        ViewGroup.LayoutParams lp=holder.length.getLayoutParams();
        lp.width=(int)(mMinItemWidth+(mMaxItemWidth/60f*getItem(position).AudioDuration));
        return convertView;
    }

    private class ViewHolder{
        TextView seconds;
        View length;
    }
}
