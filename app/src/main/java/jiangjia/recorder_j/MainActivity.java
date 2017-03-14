package jiangjia.recorder_j;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.idescout.sql.SqlScoutServer;

import java.util.ArrayList;
import java.util.List;

import jiangjia.recorder_j.FileManager.AudioEntity;
import jiangjia.recorder_j.FileManager.MyRunnable;
import jiangjia.recorder_j.View.AudioRecorderButton;
import jiangjia.recorder_j.View.MediaManager;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<AudioEntity> mAdapter;
    private List<AudioEntity> mDatas = new ArrayList<AudioEntity>();
    private String audioFilePath;

    private AudioRecorderButton mAudioRecordButtun;
    private Toolbar toolbar;
    private Menu menu;

    //确保当前只有一个音频在播放，将mAnimView设为成员变量
    private View mAnimView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        SqlScoutServer.create(this, getPackageName());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.id_listview);
        toolbar= (Toolbar) findViewById(R.id.id_toolbar);
        //toolbar.setNavigationIcon(R.mipmap.ic_previous);  // 设置导航栏图标
        setSupportActionBar(toolbar);   // 设置Toolbar支持ActionBar的一些属性

        mAudioRecordButtun = (AudioRecorderButton) findViewById(R.id.id_recorder_button);
        mAudioRecordButtun.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                AudioEntity audioEntity = new AudioEntity(seconds, filePath);

                MyRunnable insertDBRunnabl=new MyRunnable(seconds,MainActivity.this,filePath);
                new Thread(insertDBRunnabl).start();
                mDatas.add(audioEntity);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size() - 1);//定位到最后一行
            }
        });

        mAdapter = new RecorderAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAnimView != null) {
                    //停止播放动画
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                //播放动画
                mAnimView = view.findViewById(R.id.id_recorder_anim);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_history:
                Intent intent=new Intent(MainActivity.this, CircleCalendarActivity.class);
                startActivity(intent);
                return true;

            default:    // 如果用户的行为没有被执行，则会调用父类的方法去处理，建议保留。
                return super.onOptionsItemSelected(item);
        }
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
