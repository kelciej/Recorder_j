package jiangjia.recorder_j;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jiangjia.recorder_j.FileManager.AudioEntity;
import jiangjia.recorder_j.Utils.DButils;
import jiangjia.recorder_j.View.MediaManager;

public class HistoryActivity extends AppCompatActivity {

    private ListView mListView;
    private Toolbar toolbar;
    private View mAnimView;

    private List<Map<String, Object>> mDatas;
    private Context mContext;
    private int mMinItemWidth;
    private int mMaxItemWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        toolbar.setTitle("历史记录");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mDatas = getData();
        HistoryAdapter adapter = new HistoryAdapter(this);
        mListView = (ListView) findViewById(R.id.id_history_listview);
        mListView.setAdapter(adapter);

        //播放动画
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAnimView != null) {
                    //停止播放动画
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                //播放动画
                mAnimView = view.findViewById(R.id.id_history_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                //播放音频
                MediaManager.playSound(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                }, (String) mDatas.get(position).get("AudioFilePath"));
            }
        });

    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");

        DButils dButils = new DButils(getApplicationContext());
        List<AudioEntity> audioEntityList = new ArrayList<>();
        audioEntityList = dButils.findbydate(date);

        for (AudioEntity audioEntity : audioEntityList) {
            map = new HashMap<String, Object>();
            map.put("AudioDuration", audioEntity.getAudioDuration());//时间
            map.put("AudioTime", audioEntity.getTime());
            map.put("AudioFilePath", audioEntity.getFilePath());
            map.put("img", R.drawable.icon);
            list.add(map);
        }
        return list;
    }

    public class HistoryAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private class ViewHolder {
            TextView seconds;
            View length;
            TextView time;
            ImageView img;
        }

        public HistoryAdapter(Context context) {
            mContext=context;

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);

            mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
            mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
            this.mInflater = LayoutInflater.from(mContext);
            //mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            System.out.println("mDatas.size()"+mDatas.size());
            return mDatas.size();

        }

        @Override
        public Object getItem(int position) {
            return  mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_history, null);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.id_icon);
                holder.seconds = (TextView) convertView.findViewById(R.id.id_history_duration);//录音时长
                holder.length = convertView.findViewById(R.id.id_history_length);//气泡
                holder.time = (TextView) convertView.findViewById(R.id.id_history_time);//存入时间

                convertView.setTag(holder);
            } else {
                holder = (HistoryAdapter.ViewHolder) convertView.getTag();
            }
            holder.img.setBackgroundResource((Integer) mDatas.get(position).get("img"));
            //设置时间宽度
            holder.seconds.setText(Math.round((float) mDatas.get(position).get("AudioDuration")) + "\"");//求距离某数最近的整数（可能比某数大，也可能比它小）,返回int型或者long型（上一个函数返回double型）
            //holder.seconds.setText(Math.round(getItem(position).AudioDuration) + "\"");//求距离某数最近的整数（可能比某数大，也可能比它小）,返回int型或者long型（上一个函数返回double型）
            //设置存入时间
            holder.time.setText((String) (mDatas.get(position).get("AudioTime")));
            //holder.seconds.setText((Integer) mDatas.get(position).get("AudioTime") + "\"");
            //设置气泡长度
            ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
            lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * (float) mDatas.get(position).get("AudioDuration")));
            //lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * (Integer) mDatas.get(position).get("duration")));
            return convertView;
        }
    }
}