package jiangjia.recorder_j.FileManager;

/**
 * Created by KelcieJ on 2017/3/7.
 */

public class AudioEntity {

    public String filePath;
    public String time;
    int AudioID;
    public float AudioDuration;
    public AudioEntity() {
    }
    public AudioEntity(float _time, String _filePath){

        super();
        this.AudioDuration=_time;
        this.filePath=_filePath;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getAudioID() {
        return AudioID;
    }

    public void setAudioID(int audioID) {
        AudioID = audioID;
    }

    public float getAudioDuration() {
        return AudioDuration;
    }

    public void setAudioDuration(float audioDuration) {
        AudioDuration = audioDuration;
    }


}
