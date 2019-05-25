package com.example.socket;

import android.annotation.SuppressLint;
import android.net.Network;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
*   1.不是模拟机的问题
*   2.两个Message没有问题
*   3.handler处理bundle数据，结果为空？？？使用Bundle bundle = msg.getData();解决.
*   4.使用5个write，怎么第一个输出有换行?写成一行能解决吗？可以解决.
*   5.在不连接的时候关闭,out.close,in.close和socket.close即可.
* */
public class ServerActivity extends AppCompatActivity {
    private Button submit;  //提交按钮
    private EditText userText,classText,ageText,clientPort ;  //文本显示值
    private ServerSocket serverSocket;  //服务端socket
    private List<HashMap<String,Object>> mylist ;
    private Socket clientSocket;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        initView(); //初始化
        //启动服务线程.,建议使用Runnable,读取数据
        new Thread(Readerable).start(); //开启线程.
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始发送数据,先不发看看
                //现在可以发送了，发送完清空输入，并显示在下方,
                if (clientSocket==null){
                    Toast.makeText(ServerActivity.this,"等待客户端连接",Toast.LENGTH_SHORT).show();
                }else{
                    //new Thread(senderable).start();
                    Senddata senddata= new Senddata();
                    new Thread(senddata).start();
                }
            }
        });
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //what = 1,规定为发送数据.
            if (msg.what==1){
                //使用适配器.
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("User",userText.getText().toString());
                map.put("Class",classText.getText().toString());
                map.put("Age",ageText.getText().toString());
                mylist.add(map);
                SimpleAdapter adapter = new SimpleAdapter(ServerActivity.this,mylist,R.layout.item,new String[]{"User","Class","Age"},new int[]{R.id.showUser,R.id.showClass,R.id.showAge});
                listView.setAdapter(adapter);   //看看如何.
                Toast.makeText(ServerActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
            }
            //规定为读取数据.
            else if (msg.what==2){
                Bundle bundle = msg.getData();
                String result = bundle.getString("result");    //获取数据
                String[] Text = new String[3];
                //传递数据到文本中.
                assert result != null;
                //如果Text中没有出现-说明是C#发送来的.
                int i=0;
                for(i=0;i<result.length();i++){
                    if (result.charAt(i)=='-'){
                        break;
                    }
                }
                if (i==result.length()){
                    //说明是C#发送过来的，对Text进行处理.
                    Text[0] = result;
                    Text[1] = "null";
                    Text[2] = "null";
                }else{
                    Text = result.split("-");   //按分开.
                }
                //开始使用listview,传递数据到listview中
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("User",Text[0]);
                map.put("Class",Text[1]);
                map.put("Age",Text[2]);
                mylist.add(map);//绑定适配器,然而不行.
                SimpleAdapter adapter = new SimpleAdapter(ServerActivity.this,mylist,R.layout.item,new String[]{"User","Class","Age"},new int[]{R.id.showUser,R.id.showClass,R.id.showAge});
                listView.setAdapter(adapter);   //看看如何.
                Toast.makeText(ServerActivity.this,"读取成功： " +result,Toast.LENGTH_SHORT).show();
            }
            else if (msg.what==3){
                Toast.makeText(ServerActivity.this,"输入应不为空",Toast.LENGTH_SHORT).show();
                userText.setText("默认数值");  //默默更改.
                classText.setText("默认数值");
                ageText.setText("默认数值");
            }
            else if (msg.what==4){
                Toast.makeText(ServerActivity.this,"客户端已经连接",Toast.LENGTH_SHORT).show();
            }
        }
    };
    /*
    *
    * */
    private void initView(){
        userText = (EditText) findViewById(R.id.userText);  //获取user
        classText = (EditText) findViewById(R.id.classText);    //获取class
        ageText = (EditText) findViewById(R.id.ageText);    //获取age
        clientPort = (EditText)findViewById(R.id.clientport1);
        submit = (Button) findViewById(R.id.submit);    //获取提交按钮
        listView = (ListView) findViewById(R.id.listView);
        mylist = new ArrayList<HashMap<String, Object>>(50);    //new一个对象.
    }
    class Senddata implements Runnable{
        @Override
        public void run() {
            try{
                /*
                 *   发给对方,自己也显示.
                 * */
                //DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream()); //获取输入流
                OutputStream writer = clientSocket.getOutputStream();
                if (userText.getText().toString().equals("") ||classText.getText().toString().equals("") ||ageText.getText().toString().equals("") ){    //判定为空的情况.
                    Message message = new Message();
                    message.what = 3;   //为空.
                    handler.sendMessage(message);
                    Thread.sleep(500);
                }
                //连发3个，看看是如何接收的.,不是这个的问题.,下面6行正确，怎么有个换行.?
                String result = userText.getText().toString() + "-" + classText.getText().toString() + "-" + ageText.getText().toString();
                writer.write(result.getBytes());
                writer.flush(); //提交数据.
                Message message = new Message();//发送handler，更新ui
                message.what=1;//1显示正常
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    Runnable Readerable = new Runnable() {
        @Override
        public void run() {
            //跑一个线程得了.
            //客户端socket
            try {
                //serverSocket是服务器,clientSocket是客户端,难道获取输入流是服务器.getOutputStream
                int port = Integer.parseInt(clientPort.getText().toString());
                serverSocket = new ServerSocket(port); //绑定sport
                clientSocket = serverSocket.accept();   //serverSocket是该服务器的,clientSocket返回客户端的ip和端口.
                //clientSocket.toString返回的是 socket(192.168.1.107 + port)
                //userText.setText(clientSocket.toString());  报错？？？使用handle解决.
                Message message = new Message();
                message.what = 4;   //发送端口消息,客户端以连接.
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try{
                while(true){
                    /*
                    *   客户端可以给服务器发消息，服务器不能给客户端.
                    *   难道是模拟机的问题??
                    * */
                    InputStream is = clientSocket.getInputStream(); //192.168.43.224,手机的wifi ip 地址.
                    byte[] buffer = new byte[1024*50];
                    int len = is.read(buffer);
                    String result = new String(buffer, 0, len);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("result",result);  //handler处理，结果为空？？？
                    message.setData(bundle);
                    message.what =2 ;   //标识符为,读取消息.
                    handler.sendMessage(message);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    protected void onDestroy(){
        if (serverSocket!=null){
            try{
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
