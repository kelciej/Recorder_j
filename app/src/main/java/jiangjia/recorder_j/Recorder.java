package jiangjia.recorder_j;

/**
 * Created by jiangjia on 17/3/3.
 */
public class Recorder {
    float time;
    String filePath;
    public Recorder(float _time, String _filePath){

        super();
        this.time=_time;
        this.filePath=_filePath;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public float getTime() {
        return time;

    }

    public String getFilePath() {
        return filePath;
    }
}
