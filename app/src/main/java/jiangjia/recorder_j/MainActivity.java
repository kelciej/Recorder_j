package jiangjia.recorder_j;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import jiangjia.recorder_j.View.AudioRecorderButton;
import jiangjia.recorder_j.View.MediaManager;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDatas =new ArrayList<Recorder>();

    private AudioRecorderButton mAudioRecordButtun;

    //确保当前只有一个音频在播放，将mAnimView设为成员变量
    private View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.id_listview);
        mAudioRecordButtun = (AudioRecorderButton) findViewById(R.id.id_recorder_button);
        mAudioRecordButtun.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener()
        {
            @Override
            public void onFinish(float seconds, String filePath){
                Recorder recorder=new Recorder(seconds,filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size()-1);//定位到最后一行
            }
        });

        mAdapter=new RecorderAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAnimView!=null) {
                    //停止播放动画
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                //播放动画
                mAnimView =view.findViewById(R.id.id_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                //播放音频
                MediaManager.playSound(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                }, mDatas.get(position).filePath);
            }
        });
    }
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
        //MediaManager.paese();
    }

    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}
