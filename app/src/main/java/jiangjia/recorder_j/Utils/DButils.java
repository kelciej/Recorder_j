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
            "AudioTime timestamp DEFAULT(datetime('now', 'localtime')))";
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

    //数据库的构造函数，传递一个参数的， 数据库名字和版本号都写死了
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
        db.beginTransaction();
        try{

            if(db.insert(TABLE_NAME, null, values)==-1)
            {

                throw new Exception();

            }  db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            db.endTransaction();
        }
        //插入数据库中
       // db.insert(TABLE_NAME, null, values);

        System.out.println("插入成共");
        db.close();
    }
    //插入方法
    public void insert(String values){
        //获取SQLiteDatabase实例
        SQLiteDatabase db = getWritableDatabase();
        //db.execSQL("insert into "+TABLE_NAME+"(AudioPath) values('"+values+"')");
        //插入数据库中
        db.execSQL("insert into audiofile(audiopath) values(?)",new String[]{values});
        System.out.println("插入成共=============");
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

    //查找数据
    public List<AudioEntity> findbydate(String begintime,String endtime)
    {
        List<AudioEntity> AEList=new ArrayList<>();
        try{
            SQLiteDatabase db = getWritableDatabase();
            String sql = "SELECT * FROM AudioFile where AudioTime>=?"+
                    "and AudioTime<?";
            Cursor c = db.rawQuery(sql, new String[] { String.valueOf(begintime), String.valueOf(endtime) });
            if(c!=null)
            {
                while(c.moveToNext())
                {
                    AudioEntity a=new AudioEntity();
                    int AudioID = c.getInt(c.getColumnIndex("AudioID"));
                    String AudioPath=c.getString(c.getColumnIndex("AudioPath"));
                    String AudioTime=c.getString(c.getColumnIndex("AudioTime"));
                    a.setFilePath(AudioPath);
                    a.setAudioID(AudioID);
                    a.setTime(AudioTime);
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
