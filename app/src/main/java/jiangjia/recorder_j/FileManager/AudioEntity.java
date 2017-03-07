package jiangjia.recorder_j.FileManager;

/**
 * Created by KelceiJ on 2017/3/7.
 */

public class AudioEntity {
    public AudioEntity() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    private String filePath;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;

    public int getAudioID() {
        return AudioID;
    }

    public void setAudioID(int audioID) {
        AudioID = audioID;
    }

    private int AudioID;
}
