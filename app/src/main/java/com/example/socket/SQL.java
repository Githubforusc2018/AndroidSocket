package com.example.socket;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQL extends AppCompatActivity {
    List<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>(20);
    private DBOpenHelper dbOpenHelper;   //定义DBOpenHelper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sql);
        final EditText myUser = (EditText) findViewById(R.id.first_userName);
        final EditText myClass = (EditText) findViewById(R.id.first_class);
        final EditText myAge = (EditText) findViewById(R.id.first_age);
        final ListView listView = (ListView) findViewById(R.id.first_listView);
        final Button login = (Button) findViewById(R.id.first_login);
        final Button read = (Button) findViewById(R.id.first_read);
        final Button delete = (Button) findViewById(R.id.first_delete);
        dbOpenHelper = new DBOpenHelper(SQL.this, "dict.db", null, 1);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> item = new HashMap<String, Object>();//对于 HashMap 而言，系统 key-value 当成一个整体进行处理，
                // 系统总是根据 Hash 算法来计算 key-value 的存储位置，这样可以保证能快速存、取 Map 的 key-value 对。
                String User = myUser.getText().toString();
                String Class = myClass.getText().toString();
                String Age = myAge.getText().toString();
                if (User.equals("") || Class.equals("") || Age.equals("")){
                    Toast.makeText(SQL.this,"输入不应该为空",Toast.LENGTH_SHORT).show();
                }else{
                    item.put("user",myUser.getText().toString());   //myUser.getText()时数据会重复添加，toString()后数据不重复添加.????
                    item.put("class",myClass.getText().toString()); //返回对象的字符串表示.
                    item.put("age",myAge.getText().toString());
                    data.add(item);
                    //Log.v(LogDemo.ACTIVITY_TAG, "This is Verbose.");
                    //当前活动,数据源,item布局文件,显示对应.
                    SimpleAdapter adapter = new SimpleAdapter(SQL.this,data,R.layout.item,new String[]{"user","class","age"},new int[]{R.id.showUser,R.id.showClass,R.id.showAge});
                    listView.setAdapter(adapter);
                    //插入数据库.
                    insertData(dbOpenHelper.getReadableDatabase(),User,Class,Age);
                }
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取数据.
                SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from dict",null);
                if (cursor!=null&&cursor.getCount()>0 ){
                    while(cursor.moveToNext()){
                        HashMap<String, Object> item = new HashMap<String, Object>();//对于 HashMap 而言，系统 key-value 当成一个整体进行处理，
                        item.put("user",cursor.getString(0));   //myUser.getText()时数据会重复添加，toString()后数据不重复添加.????
                        item.put("class",cursor.getString(1));   //myUser.getText()时数据会重复添加，toString()后数据不重复添加.????
                        item.put("age",cursor.getString(2));   //myUser.getText()时数据会重复添加，toString()后数据不重复添加.????
                        data.add(item);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(SQL.this,data,R.layout.item,new String[]{"user","class","age"},new int[]{R.id.showUser,R.id.showClass,R.id.showAge});
                    listView.setAdapter(adapter);
                }
                db.close();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除条件，默认删除有的，如先读取到的最新的，按年龄删除.
                String Age = myAge.getText().toString();
                SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
                db.execSQL("delete from dict where Age = " + Age);
                db.close();
                Toast.makeText(SQL.this,"删除成功",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void insertData(SQLiteDatabase readableDatabase,String User,String Class,String Age){
        //全部为String
        ContentValues values = new ContentValues(); //跟HashTable差不多，但只能存储基本的数据类型，如String ,int
        values.put("User",User);
        values.put("Class",Class);
        values.put("Age",Age);
        readableDatabase.insert("dict",null,values);//contenvalues只能存储基本类型的数据，像string，int之类的
    }
}
