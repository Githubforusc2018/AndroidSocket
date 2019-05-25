package com.example.socket;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private final String CREATE_TABLE_SQL = "create table dict(User varchar(25) ,Class varchar(25),Age integer(5))";
    public DBOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){

        super(context,name,null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL);   //看看是不是这样.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    //getWritableDatabase()
    //它会调用并返回一个可以读写数据库的对象
     //       在第一次调用时会调用onCreate的方法
    //当数据库存在时会调用onOpen方法
    //        结束时调用onClose方法
    //getReadableDatabase()
    //它会调用并返回一个可以读写数据库的对象
    //        在第一次调用时会调用onCreate的方法
   // 当数据库存在时会调用onOpen方法
     //       结束时调用onClose方法
    //两个方法都是返回读写数据库的对象，但是当磁盘已经满了时，getWritableDatabase会抛异常，而getReadableDatabase不会报错，它此时不会返回读写数据库的对象，而是仅仅返回一个读数据库的对象
}
