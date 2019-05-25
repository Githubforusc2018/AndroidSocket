package com.example.socket;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    int [] image = new int[]{R.drawable.image1,R.drawable.image2,R.drawable.image3,R.drawable.image4};
    private ViewFlipper viewFlipper;
    private TextView [] textView ;
    private Animation[] animation = new Animation[2];   //动画数组
    private static int next = 0,last=0;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //换图片.
            //mod 4取余.

            if (msg.what==1){
                //next = 0,last = 3
                //next = 1,last = 0;
                //next = 2,last = 1;
                //next = 3,last = 2;
                //next = 0,last = 3;
                viewFlipper.showPrevious();
                Message message;
                message=handler.obtainMessage(1);   //获取要发送的消息
                handler.sendMessageDelayed(message, 1500);  //延迟3秒发送消息
            }
            if (msg.what==2){
                textView[next].setTextColor(Color.parseColor("#DB1B60"));    //设置颜色.
                textView[next].setTextSize(31);    //设置大小.
                if (next==0){
                    //说明时第0个,last = 3;
                    last = 3;
                }else{
                    last = next - 1;
                }
                textView[last].setTextColor(Color.parseColor("#FFFFFF"));    //设置颜色，上一个.
                textView[last].setTextSize(17);    //设置大小.
                //my count = 当前
                next++;
                if (next==4){
                    next = 0;
                }
                Message message;
                message=handler.obtainMessage(2);   //获取要发送的消息
                handler.sendMessageDelayed(message, 1000);  //延迟3秒发送消息
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button client = (Button) findViewById(R.id.client);
        Button server = (Button) findViewById(R.id.server);
        Button listViewServer = (Button) findViewById(R.id.listViewServer);
        Button listViewClient = (Button) findViewById(R.id.listViewClient);
        Button listViewSQL = (Button) findViewById(R.id.listviewsql);
        textView = new TextView[4];
        textView[0] = (TextView)findViewById(R.id.text1);
        textView[1] = (TextView)findViewById(R.id.text2);
        textView[2] = (TextView)findViewById(R.id.text3);
        textView[3] = (TextView)findViewById(R.id.text4);
        viewFlipper = (ViewFlipper)findViewById(R.id.viewFlipper);
        //添加图片
        for(int i=0;i<image.length;i++){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(image[i]);
            viewFlipper.addView(imageView);
        }
        animation[0] = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
        animation[1] = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);
        viewFlipper.setInAnimation(animation[0]);
        viewFlipper.setOutAnimation(animation[1]);
        Message message;
        message = Message.obtain();
        message.what = 1;
        handler.sendMessage(message);   //发送消息

        Message message1;
        message1 = Message.obtain();
        message1.what=2;
        handler.sendMessage(message1);   //发送消息
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ClientActivity.class);
                startActivity(intent);  //与c#通信客户端
            }
        });
        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,normalServer.class);
                startActivity(intent);  //c#通信服务器
            }
        });
        listViewClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,listViewClient.class);
                startActivity(intent);  //Android通信客户端
            }
        });

        listViewServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ServerActivity.class);
                startActivity(intent);  //Android通信服务器
            }
        });
        listViewSQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SQL.class);
                startActivity(intent);  //Android通信服务器
            }
        });
    }
}
