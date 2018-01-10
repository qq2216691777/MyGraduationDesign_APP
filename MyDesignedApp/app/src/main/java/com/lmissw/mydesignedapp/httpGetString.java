package com.lmissw.mydesignedapp;

import com.lmissw.mydesignedapp.MainActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ZhouWangsheng on 2018/1/9.
 */

public class httpGetString {

    private static String urlString = null;
    private static String httpUrl = null;

    private InputStream outStream = null;
    private static String outString = null;

    public static String getString( String urlStr )
    {
        if(urlStr==null)
            return null;
        urlString=urlStr;
        outString = null;
        Thread httpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(urlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Charset", "UTF-8");  // 设置字符集
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    int code = connection.getResponseCode();

                    if (code == 200) {
                        InputStream outStream = connection.getInputStream();
                        int fileLength = connection.getContentLength();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(outStream));
                        StringBuilder jsonText = new StringBuilder();
                        String line = null;
                        try {
                            while ((line = reader.readLine()) != null) {
                                jsonText.append(line + "/n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                outStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            outString = jsonText.toString();
                        }
                        Log.i("Infor", "请求成功 "+fileLength);
                    } else {
                        Log.i("Infor", "请求失败");
                    }
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                        Log.i("Infor", "disconnect ok");
                    }
                }
            }
        });
        httpThread.start();
        while(httpThread.isAlive());
        Log.i("Infor", "httpData:"+outString);
        return outString;
    }


}

