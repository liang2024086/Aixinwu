package com.aixinwu.axw.tools;

/**
 * Created by Cross_Life on 2016/7/24.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.aixinwu.axw.tools.MyDBOpenHelper;
import com.aixinwu.axw.tools.dbmessage;

public class dbmessage {
    private static final String TAG = "PersonDao";
    private MyDBOpenHelper dbOpenHelper;

    // 在personDao被new出来的时候 就完成初始化

    public dbmessage(Context context) {
        dbOpenHelper = new MyDBOpenHelper(context);
        // dbOpenHelper.getReadableDatabase()
        // dbOpenHelper.getWritableDatabase()
    }

    // 增删改查

    /**
     * 往数据库添加一条数据
     */
    public void add(int send, int recv, String doc) {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into message (send,recv,doc) values (?,?,?)",
                    new Object[] { send, recv, doc});
            // 关闭数据库 释放数据库的链接
            db.close();
        }
    }


    public List<talkmessage> getIn(String sender, String recver){
        List<talkmessage> newmessage = null;
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        if (db.isOpen()){
            newmessage = new ArrayList<talkmessage>();
            Cursor cursor =db.rawQuery("select * from message where (send=? and recv=?) or (send=? and recv=?)", new String[] {sender, recver,recver,sender});
            while (cursor.moveToNext()){
                talkmessage DbMessage = new talkmessage();
                int sendindex = cursor.getColumnIndex("send");
                int recvindex = cursor.getColumnIndex("recv");
                int docindex = cursor.getColumnIndex("doc");
                int Sender = cursor.getInt(sendindex);
                int Recver = cursor.getInt(recvindex);
                String Docer = cursor.getString(docindex);
                DbMessage.setDoc(Docer);
                DbMessage.setReceiver(Recver);
                DbMessage.setSender(Sender);
                newmessage.add(DbMessage);
            }
            cursor.close();
            db.close();
        }

        return newmessage;
    }


    public List<talkmessage> getIntalk(String mem){
        List<talkmessage> newmessage = null;
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        if (db.isOpen()){
            newmessage = new ArrayList<talkmessage>();
            Cursor cursor =db.rawQuery("select * from message where (send=? or recv=?)", new String[] {mem, mem});
            while (cursor.moveToNext()){
                talkmessage DbMessage = new talkmessage();
                int sendindex = cursor.getColumnIndex("send");
                int recvindex = cursor.getColumnIndex("recv");
                int docindex = cursor.getColumnIndex("doc");
                int Sender = cursor.getInt(sendindex);
                int Recver = cursor.getInt(recvindex);
                String Docer = cursor.getString(docindex);
                DbMessage.setDoc(Docer);
                DbMessage.setReceiver(Recver);
                DbMessage.setSender(Sender);
                newmessage.add(DbMessage);
            }
            cursor.close();
            db.close();
        }

        return newmessage;
    }


}