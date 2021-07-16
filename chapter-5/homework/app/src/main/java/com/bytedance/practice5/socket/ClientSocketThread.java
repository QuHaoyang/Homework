package com.bytedance.practice5.socket;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.practice5.UploadActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientSocketThread extends Thread {
    public ClientSocketThread(SocketActivity.SocketCallback callback) {
        this.callback = callback;
    }

    private SocketActivity.SocketCallback callback;

    //head请求内容
    private static String content = "HEAD / HTTP/1.1\r\nHost:www.zju.edu.cn\r\n\r\n";


    @Override
    public void run() {
        try{
            byte[] data = new byte[1024*5];
            Socket socket = new Socket("www.zju.edu.cn",80);
            BufferedOutputStream outputStream = new BufferedOutputStream(socket.getOutputStream());
            BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream.write(content.getBytes("utf-8"));
            outputStream.flush();
            int resultLen = inputStream.read(data);
            String result = new String(data,0,resultLen);
            callback.onResponse(result);
            inputStream.close();
            outputStream.close();
            socket.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // TODO 6 用socket实现简单的HEAD请求（发送content）
        //  将返回结果用callback.onresponse(result)进行展示
    }
}