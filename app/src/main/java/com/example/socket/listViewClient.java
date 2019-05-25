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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//专门使用listview情况,其他情况不适用.
public class listViewClient extends AppCompatActivity {
    private EditText clientIp,clientPort;
    private EditText clientUserText,clientClassText,clientAgeText;
    private Button clientSubmit,clientConnect;  //提交和连接.
    private ListView listView;
    private Socket mysocket;
    private List<HashMap<String,Object>> mylist ;
    private static boolean flag = true;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //发送消息
            if (msg.what==1){
                //使用适配器.
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("User",clientUserText.getText().toString());
                map.put("Class",clientClassText.getText().toString());
                map.put("Age",clientAgeText.getText().toString());
                mylist.add(map);
                SimpleAdapter adapter = new SimpleAdapter(listViewClient.this,mylist,R.layout.item,new String[]{"User","Class","Age"},new int[]{R.id.showUser,R.id.showClass,R.id.showAge});
                listView.setAdapter(adapter);   //看看如何.
                Toast.makeText(listViewClient.this,"发送成功",Toast.LENGTH_SHORT).show();
            }
            //读取消息
            else if (msg.what==2){
                //读取数据
                Bundle bundle = msg.getData();
                String result = bundle.getString("result");    //获取数据
                String[] Text = new String[3];
                //传递数据到文本中.
                assert result != null;
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
                HashMap<String,Object> map = new HashMap<String, Object>();
                map.put("User",Text[0]);
                map.put("Class",Text[1]);
                map.put("Age",Text[2]);
                mylist.add(map);
                SimpleAdapter adapter = new SimpleAdapter(listViewClient.this,mylist,R.layout.item,new String[]{"User","Class","Age"},new int[]{R.id.showUser,R.id.showClass,R.id.showAge});
                listView.setAdapter(adapter);   //看看如何.
                Toast.makeText(listViewClient.this,"读取成功： " +result,Toast.LENGTH_SHORT).show();
            }
            else if (msg.what==3){
                Toast.makeText(listViewClient.this,"输入应不为空",Toast.LENGTH_SHORT).show();
                clientUserText.setText("默认数值");  //默默更改.
                clientClassText.setText("默认数值");
                clientAgeText.setText("默认数值");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_client);
        initView();
        Toast.makeText(this,"开始连接",Toast.LENGTH_SHORT).show();
        clientConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(Readable).start();
            }
        });
        clientSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先点连接，才能发送.
                if (mysocket==null){
                    Toast.makeText(listViewClient.this,"必须先创建一个Socket才能连接",Toast.LENGTH_SHORT).show();
                }else{
                    new Thread(Sendable).start();
                }
            }
        });
    }
    private void initView(){
        clientIp = (EditText) findViewById(R.id.clientIp);              //IP
        clientPort = (EditText) findViewById(R.id.clientPort);          //端口
        clientUserText = (EditText) findViewById(R.id.clientUserText);    //用户
        clientClassText = (EditText) findViewById(R.id.clientClassText);        //班级
        clientAgeText = (EditText) findViewById(R.id.clientAgeText);      //年龄

        //提交按钮
        clientSubmit = (Button) findViewById(R.id.clientSubmit);
        //连接按钮
        clientConnect = (Button) findViewById(R.id.clientConnect);

        //ListView
        listView = (ListView) findViewById(R.id.listView);
        mylist = new ArrayList<HashMap<String, Object>>();

    }
    Runnable Sendable = new Runnable() {
        @Override
        public void run() {
            try{
                /*
                 * DataOutputStream out = new DataOutputStream(OutputStream  out),数据输出流
                 * */
                //DataOutputStream writer = new DataOutputStream(mysocket.getOutputStream());
                OutputStream writer = mysocket.getOutputStream();
                if (clientUserText.getText().toString().equals("") ||clientClassText.getText().toString().equals("") ||clientAgeText.getText().toString().equals("") ){    //判定为空的情况.
                    Message message = new Message();
                    message.what = 3;   //为空.
                    handler.sendMessage(message);
                    Thread.sleep(500);
                }
                String result = clientUserText.getText().toString() + "-" + clientClassText.getText().toString() + "-" + clientAgeText.getText().toString();
                writer.write(result.getBytes());
                writer.flush();
                /*
                *   问题，如何发送一个数组数据呢.
                * */
                //取消了handler???
                //发送，我也要写入handler中.
                Message message = new Message();    //一个handler对应一个message
                message.what = 1;   //标识符为1
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Runnable Readable = new Runnable() {
        @Override
        public void run() {
            //这里若对象反复创建了,需要用标记解决,
            if (flag){
                try{
                    int port = Integer.parseInt(clientPort.getText().toString());   //先连接.
                    mysocket = new Socket(clientIp.getText().toString(),port); //连成功了，服务器一直显示数据.
                    //mysocket返回的是服务器的ip + 端口
                }catch (UnknownHostException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
            flag = false;
            try{
                while(true){
                    InputStream is = mysocket.getInputStream(); //192.168.43.224,手机的wifi ip 地址.
                    /*InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    br.readLine();*/
                    byte[] buffer = new byte[1024*50];
                    int len = is.read(buffer);
                    String result = new String(buffer, 0, len);
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("result",result);
                    message.setData(bundle);
                    message.what = 2;   //标识符为1,现在改为3看看是不是列表视图的冲突.,现在又改回来了,读取消息.
                    handler.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
