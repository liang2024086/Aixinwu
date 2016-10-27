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
    public void add(int send, int recv, String doc, int isRead, String time) {

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        String mytime = "";
        mytime += time.substring(0,10)+" "+time.substring(11,19);
        if (db.isOpen()) {
            db.execSQL("insert into message (send,recv,doc,isRead,time) values (?,?,?,?,?)",
                    new Object[] { send, recv, doc, isRead,mytime});
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
                int messageIdindex =cursor.getColumnIndex("messageid");
                int sendindex = cursor.getColumnIndex("send");
                int recvindex = cursor.getColumnIndex("recv");
                int docindex = cursor.getColumnIndex("doc");
                int Sender = cursor.getInt(sendindex);
                int Recver = cursor.getInt(recvindex);
                int Messageid = cursor.getInt(messageIdindex);
                String Docer = cursor.getString(docindex);
                DbMessage.setMessageid(Messageid);
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
                int messageIdindex =cursor.getColumnIndex("messageid");
                int sendindex = cursor.getColumnIndex("send");
                int recvindex = cursor.getColumnIndex("recv");
                int docindex = cursor.getColumnIndex("doc");
                int timeIndex = cursor.getColumnIndex("time");
                int Sender = cursor.getInt(sendindex);
                int Recver = cursor.getInt(recvindex);
                int Messageid = cursor.getInt(messageIdindex);
                String Docer = cursor.getString(docindex);
                DbMessage.setDoc(Docer);
                DbMessage.setMessageid(Messageid);
                DbMessage.setReceiver(Recver);
                DbMessage.setSender(Sender);
                DbMessage.setTime(cursor.getString(timeIndex));
                newmessage.add(DbMessage);
            }
            cursor.close();
            db.close();
        }

        return newmessage;
    }
    public void update(int messageid){
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("update message set isRead = 1 where (messageid = ?)",
                    new String[] { Integer.toString(messageid)});
            // 关闭数据库 释放数据库的链接
            db.close();
        }
    }
    public int count(int recv){
        List<talkmessage> newmessage = null;
        SQLiteDatabase db= dbOpenHelper.getReadableDatabase();
        if (db.isOpen()){

            Cursor cursor =db.rawQuery("select * from message where ((recv=? or send = ?) and isRead = 0)", new String[] {Integer.toString(recv), Integer.toString(recv)});
            int ans = cursor.getCount();

            cursor.close();
            db.close();
            return ans;
        }

        return 0;
    }

}