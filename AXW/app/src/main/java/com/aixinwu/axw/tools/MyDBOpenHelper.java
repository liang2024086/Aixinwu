package com.aixinwu.axw.tools;

/**
 * Created by Cross_Life on 2016/7/24.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBOpenHelper extends SQLiteOpenHelper {

    /**
     *
     * @param context 应用程序上下文

     */
    public MyDBOpenHelper(Context context) {
        super(context, "talk.db", null, 6);
    }

    // 在mydbOpenHelper 在数据库第一次被创建的时候  会执行onCreate();
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("我被调用了 oncreate");
        db.execSQL("CREATE TABLE message (messageid integer primary key autoincrement, send integer, recv integer, doc varchar(40),time TEXT, isRead integer)");
    }
    // 通过version的增加来执行数据库版本更新，版本号改为6的同时，调用onUpgrade ，让程序员执行具体更新；
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("on update ");
        //   db.execSQL("ALTER TABLE person ADD phone VARCHAR(12) NULL ");
    }
}
