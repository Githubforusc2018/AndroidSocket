package com.example.socket;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class normalServer extends AppCompatActivity {
    private ServerSocket serverSocket;  //服务端socket
    Socket clientSocket;
    private TextView normalText;
    private EditText normalMessage;
    private Button normalSubmit ;
    private StringBuffer sb = new StringBuffer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_server);
        //一般的服务端,直接等待连接.
        initView();
        new Thread(Readerable).start(); //开启线程.
        normalSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Senddata1 senddata= new Senddata1();
                new Thread(senddata).start();
            }
        });
    }
    public void initView(){
        normalText = (TextView) findViewById(R.id.normalText);  //获取user
        normalMessage = (EditText) findViewById(R.id.normalMessage);    //获取class
        normalSubmit = (Button) findViewById(R.id.normalSubmit);    //获取提交按钮
    }
    Runnable Readerable = new Runnable() {
        @Override
        public void run() {
            //跑一个线程得了.
            //客户端socket
            try {
                serverSocket = new ServerSocket(50000); //绑定sport
                clientSocket = serverSocket.accept();   //serverSocket是该服务器的,clientSocket返回客户端的ip和端口.
                //clientSocket.toString返回的是192.168.1.107 + port
                //userText.setText(clientSocket.toString());  报错？？？
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try{
                while(true){
                    /*
                     *   客户端给服务器可以，服务器不能给客户端.
                     *
                     * */
                    //监听手机50000端口.
                    InputStream is = clientSocket.getInputStream();
                    byte[] buffer = new byte[1024*1024];
                    int len = is.read(buffer);
                    String result = new String(buffer, 0, len);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("msg",result);
                    message.setData(bundle);
                    message.what = 1;   //标识符为1
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
    class Senddata1 implements Runnable{
        @Override
        public void run() {
            try{
                /*
                 *   发给对方,自己也显示.
                 * */
                //DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream()); //获取输入流
                OutputStream writer = clientSocket.getOutputStream();
                writer.write(normalMessage.getText().toString().getBytes());   //发给1看看.
                String result = "发送成功";
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("msg",result);
                message.setData(bundle);
                //发送handler，更新ui
                message.what=1;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Bundle data = msg.getData();
                sb.append(data.getString("msg"));
                sb.append("\n");
                normalText.setText(sb.toString());
            }
        }
    };
}
