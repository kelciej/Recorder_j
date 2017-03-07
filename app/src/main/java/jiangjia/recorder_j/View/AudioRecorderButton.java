package jiangjia.recorder_j.View;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import jiangjia.recorder_j.R;

/**
 * Created by KelceiJ on 2017/3/7.
 */

public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener {
    private static final int DISTANCE_Y_CANCEL=50; //正常情况下此处为dp，然后通过方法转换为px
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;

    private static final int MSG_AUDIO_PREPARED=0X110;
    private static final int MSG_VOICE_CHANGED=0X111;
    private static final int MSG_DIALOG_DIMISS=0X112;

    private int mCurState = STATE_NORMAL;
    //已经开始录音
    private boolean isRecording = false;
    private boolean mReady;//是否触发longclik
    private DialogManager mDialogManager;
    private AudioManager mAudioManager;
    private Float mTime=0f;//计时展示音量显示变动
    public AudioRecorderButton(Context context) {
        this(context, null);//默认调用两个参数的构造方法
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化dialog
        mDialogManager=new DialogManager(getContext());
        //保存路径
        String dir= Environment.getExternalStorageDirectory()+"/recorder_audios";
        if (!Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
        {
            System.out.println("SDcard err!");
        }
        System.out.println("dir"+dir);
        mAudioManager=AudioManager.getInstance(dir);//实例化
        mAudioManager.setOnAudioStateListener(this);
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //TODO在audio end prepare 之后
                mReady=true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     */
    public interface AudioFinishRecorderListener{
        void onFinish(float seconds, String filePath);
    }

    private AudioFinishRecorderListener mListener;
    public  void setAudioFinishRecorderListener(AudioFinishRecorderListener listener){
        mListener=listener;
    }

    /**
     * 获取音量大小的Runnable
     */
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while(isRecording) {
                try {
                    Thread.sleep(100);
                    System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhh");

                    mTime += 0.1f;
                    handler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //显示应该在audio end prepared以后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED:
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dismissDialog();
                    break;
            }
        }
    };
    @Override
    public void wellPrepared() {
        handler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //isRecording=true;
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                //根据x，y，的坐标判断是否想要取消
                if (isRecording) {   //已经开始录音为前提
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                //正常结束
                if (mCurState == STATE_RECORDING) {
                    //如果longclik都没有触发
                    if (!mReady) {
                        reset();
                        return super.onTouchEvent(event);
                    }
                    if (!isRecording||mTime<0.6f) {

                        System.out.println("sssssssssssssssssssssssssssssssss");
                        mDialogManager.tooShort();
                        if(mAudioManager!=null) //应该加上这个判断，不然有可能mAudioManage未创建完成
                        {
                            mAudioManager.cancel();
                            handler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                        }
                        //mDialogManager.dismissDialog();
                    }
                    else if (mCurState == STATE_RECORDING) {//录制正常结束
                        if(mListener != null){
                            mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                        }
                        mDialogManager.dismissDialog();
                        mAudioManager.release();
                        //callbackToAct
                    }

                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    mDialogManager.dismissDialog();
                    mAudioManager.cancel();
//                    mDialogManager.wantToCancel();
                }
                reset();//重置标志
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean wantToCancel(int x, int y) {
        if(x<0||x>getWidth())
        {
            return true;
        }
        if(y<-DISTANCE_Y_CANCEL||y>getHeight()+DISTANCE_Y_CANCEL)
        {
            return true;
        }
        return false;
    }

    private void changeState(int state) {
        if(mCurState!=state){
            mCurState=state;
            switch (state)
            {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recordering);
                    setText(R.string.str_recorder_recording);
                    if(isRecording){
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_wantcancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    //恢复状态位和标志
    private void reset() {
        mTime=0F;
        mReady=false;
        isRecording = false;
        changeState(STATE_NORMAL);
    }

}
