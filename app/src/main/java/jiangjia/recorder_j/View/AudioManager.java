package jiangjia.recorder_j.View;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by KelcieJ on 2017/3/7.
 */

public class AudioManager {
    private MediaRecorder mediaRecorder;
    private String mDir;
    private String mCurrentFilePath;
    private static AudioManager Instance;
    private boolean isPrepared=false;

    private Context context;

    public AudioManager(String dir){
        this.mDir=dir;
    }

    /**
     * 回调,准备完毕
     */
    public interface AudioStateListener{
        void wellPrepared();
    }

    public AudioStateListener mListener;
    public void setOnAudioStateListener(AudioStateListener listener){
        mListener=listener;
    }



    //单例模式
    public static AudioManager getInstance(String dir){
        if(Instance == null)
        {
            synchronized (AudioManager.class){
                if (Instance == null) {
                    Instance=new AudioManager(dir);
                }
            }
        }
        return Instance;
    }

    public void prepareAudio(){
        try {
            isPrepared=false;
            //准备录音文件路径
            File dir=new File(mDir);
            if(!dir.exists()){
                dir.mkdir();
            }

            String filename=getFileName();
            File file=new File(dir,filename);

            mCurrentFilePath=file.getAbsolutePath();


            mediaRecorder = new MediaRecorder();
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            //准备结束
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置音频源为麦克风
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);//设置输出格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            isPrepared=true;
            if(mListener!=null){
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getFileName() {
        return UUID.randomUUID().toString()+".amr";
    }

    public void release(){
        //callback
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder=null;

    }

    public void cancel(){
        release();
        if(mCurrentFilePath!=null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    public int getVoiceLevel(int max){
        if(isPrepared){
            try {
                //getMaxAmplitude() 1-32767
                //此处mMediaRecorder.getMaxAmplitude 只能取一次，如果前面取了一次，后边再取就为0了
                return ((max * mediaRecorder.getMaxAmplitude()) / 32768) + 1;
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        return 1;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }
}
