package jiangjia.recorder_j.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import jiangjia.recorder_j.FileManager.AudioEntity;

/**
 * Created by KelcieJ on 2017/3/7.
 * 数据库操作类
 */
public class DButils extends SQLiteOpenHelper {
    private final static int VERSION = 1;
    private final static String DB_NAME = "Audio.db";
    private final static String TABLE_NAME = "AudioFile";
    private final static String CREATE_TBL = "create table if not exists AudioFile(AudioID integer primary key autoincrement, " +
            "AudioPath varchar(50), " +
            "AudioTime timestamp DEFAULT(datetime('now', 'localtime')), "+
            "AudioDuration float)";
    private SQLiteDatabase db;

    //SQLiteOpenHelper子类必须要的一个构造函数
    public DButils(Context context, String name,  CursorFactory factory, int version) {
        //必须通过super 调用父类的构造函数
        super(context, name, factory, version);
    }

    //数据库的构造函数，传递三个参数的
    public DButils(Context context, String name, int version){
        this(context, name, null, version);
    }

    //数据库的构造函数，传递一个参数的
    public DButils(Context context){
        this(context, DB_NAME, null, VERSION);
    }

    // 回调函数，第一次创建时才会调用此函数，创建一个数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        System.out.println("Create Database");
        db.execSQL(CREATE_TBL);
    }

    //回调函数，当你构造DBHelper的传递的Version与之前的Version调用此函数
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("update Database");

    }

    //插入方法
    public void insert(ContentValues values){
        //获取SQLiteDatabase实例
        SQLiteDatabase db = getWritableDatabase();
        //插入数据库中
        db.insert(TABLE_NAME, null, values);
        System.out.println("插入成功");
        db.close();
    }

    //查询方法
    public Cursor query(){
        SQLiteDatabase db = getReadableDatabase();
        //获取Cursor
        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null, null);
        return c;

    }

    //根据唯一标识_id  来删除数据
    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, "_id=?", new String[]{String.valueOf(id)});
    }


    //更新数据库的内容
    public void update(ContentValues values, String whereClause, String[]whereArgs){
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    //关闭数据库
    public void close(){
        if(db != null){
            db.close();
        }
    }

    public List<String> queryCalendar(){
        List<String> list=new ArrayList<String>();
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT AudioTime FROM AudioFile";
        Cursor c = db.rawQuery(sql,null);
        if(c!=null)
        {
            int i=0;
            while(c.moveToNext())
            {
                String time = c.getString(c.getColumnIndex("AudioTime"));
                String temp=time.substring(0,time.indexOf(" "));//获得完整的 日期
                if(i==0)
                {
                    list.add(temp);
                    i++;
                }
                else if(!temp.equals(list.get(i-1))){//或者前一个数和本身不同，加入列表，减少冗余
                    list.add(temp);
                    i++;
                }
            }
            c.close();
        }
        return list;
    }

    //查找数据
    public List<AudioEntity> findbydate(String begintime)
    {
        List<AudioEntity> AEList=new ArrayList<>();
        try{
            SQLiteDatabase db = getWritableDatabase();
            String sql = "SELECT * FROM AudioFile where DATE(AudioTime)= DATE(?)"+
                    " order by AudioTime desc";
            Cursor c = db.rawQuery(sql,new String[] { begintime+"T" });
            if(c!=null)
            {
                while(c.moveToNext())
                {
                    AudioEntity a=new AudioEntity();
                    int AudioID = c.getInt(c.getColumnIndex("AudioID"));
                    String AudioPath=c.getString(c.getColumnIndex("AudioPath"));
                    String AudioTime=c.getString(c.getColumnIndex("AudioTime"));
                    float AudioDuration=c.getFloat(c.getColumnIndex("AudioDuration"));
                    a.setFilePath(AudioPath);
                    a.setAudioID(AudioID);
                    a.setTime(AudioTime);
                    a.setAudioDuration(AudioDuration);
                    AEList.add(a);
                }
                c.close();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return AEList;
    }
}
