package com.example.socket;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientActivity extends AppCompatActivity {
    private EditText clientIp,clientMessage,clientPort;
    private Button clientSubmit,clientConnect;
    private TextView serverMessageText;
    private Socket mysocket;
    private static int mycount = 1;
    private String myIp;
    private StringBuffer sb = new StringBuffer();
    /*
    *   当对字符串进行修改的时候，需要使用 StringBuffer 和 StringBuilder 类
    *
    * */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                Bundle data = msg.getData();
                sb.append(data.getString("msg"));
                sb.append("\n");
                serverMessageText.setText(sb.toString());
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        initView();
        Toast.makeText(ClientActivity.this,"开始连接",Toast.LENGTH_SHORT).show();
        clientConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(Readable).start();
            }
        });
        clientSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clientMessage.getText().toString().length()==0){
                    return ;
                }
                new Thread(Sendable).start();
            }
        });
    }
    private void initView(){
        clientIp = (EditText) findViewById(R.id.clientIp);
        clientMessage = (EditText) findViewById(R.id.clientMessage);
        clientSubmit = (Button) findViewById(R.id.clientSubmit);
        clientConnect = (Button) findViewById(R.id.clientConnect);
        serverMessageText = (TextView)findViewById(R.id.serverMessageText);
        clientPort = (EditText) findViewById(R.id.clientPort);
    }
    Runnable Sendable = new Runnable() {
        @Override
        public void run() {
            try{
                /*
                 * DataOutputStream out = new DataOutputStream(OutputStream  out),数据输出流
                 * */

                DataOutputStream writer = new DataOutputStream(mysocket.getOutputStream());
                writer.write(clientMessage.getText().toString().getBytes());
                //发送，我也要写入handler中.
                Message message = new Message();
                message.what = 1;   //标识符为1
                Bundle bundle = new Bundle();
                bundle.putString("msg", myIp+" : "+clientMessage.getText().toString());  //ip + 说明
                message.setData(bundle);
                handler.sendMessage(message);
                //writer.writeUTF(str);   //写一个utf-8的信息
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable Readable = new Runnable() {
        @Override
        public void run() {
            try{
                int port = Integer.parseInt(clientPort.getText().toString());
                mysocket = new Socket(clientIp.getText().toString(),port); //连成功了，服务器一直显示数据.
            }catch (UnknownHostException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            try{
                while(true){
                    InputStream is = mysocket.getInputStream(); //192.168.43.224,手机的wifi ip 地址.
                    byte[] buffer = new byte[1024*1024];
                    int len = is.read(buffer);
                    String result = new String(buffer, 0, len);
                    /*  第一次读到的是Ip地址，必须保存.*/
                    if (mycount++==1){
                        myIp = result;
                    }
                    Message message = new Message();
                    message.what = 1;   //标识符为1
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", result);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
