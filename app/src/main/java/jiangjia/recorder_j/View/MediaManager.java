package jiangjia.recorder_j.View;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by KelcieJ on 2017/3/7.
 */

public class MediaManager{
    //可以通过调用MediaPlayer.setOnCompletionListener(OnCompletionListener)方法来设置。
    // 内部的播放引擎一旦调用了OnCompletion.onCompletion()回调方法，说明这个MediaPlayer
    // 对象进入了PlaybackCompleted状态。
    // 9.3) 当处于PlaybackCompleted状态的时候，可以再调用start()方法来让这个MediaPlayer
    // 对象再进入Started状态。


    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;

    public static void playSound(MediaPlayer.OnCompletionListener onCompletionListener, String filePath) {
        if (mMediaPlayer==null) {
            mMediaPlayer = new MediaPlayer();
            //设置异常
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }


        try {
            //设置媒体流类型
            mMediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //音频停止
    public static void pause(){
        if((mMediaPlayer!=null)&&mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    //音频恢复播放
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            // 调用start()方法会让一个处于Paused状态的MediaPlayer对象从之前暂停的地方恢复播放。
            // 当调用start()方法返回的时候，MediaPlayer对象的状态会又变成Started状态。
            mMediaPlayer.start();
        }
    }

    public static void release(){
        if(mMediaPlayer != null){
            mMediaPlayer.release();
            mMediaPlayer=null;
        }
    }
}
