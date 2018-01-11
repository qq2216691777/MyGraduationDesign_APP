package com.lmissw.mydesignedapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.xml.transform.Templates;

/**
 * Created by ZhouWangsheng on 2018/1/8.
 */

class weatherData {
    public String type = null;
    public String updateTime = null;
    public int temper=0;
    public int temperMin=0;
    public int temperMax=0;
    public int humidity = 0;
    public String windpower = null;
    public String windDir = null;
    public String pic = null;
}

public class getWeather extends MainActivity {

    private final static String httpCity = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js";
    private final static String httpWeather = "http://wthrcdn.etouch.cn/WeatherApi?city=";
    private final static String httpWeather2 = "http://www.weather.com.cn/data/cityinfo/";

    private static String localProvince = null; //需要通过网络获取当前所在省份
    private static String localCity = null; //需要通过网络获取当前所在城市
    private static String localCityID = null;

    public weatherData today = null;

    private static Context thisApp=null;
//    weatherData yesterday = null;
//    weatherData tommorrow = null;

    public String getCity()
    {
        return localCity;
    }


    private static getWeather iweather = null;
    public static  getWeather getInstace( Context myContext ){
        thisApp = myContext;
        if( iweather == null )
        {
            iweather = new getWeather();
        }

        return iweather;
    }

    private getWeather() {

        today = new weatherData();
        //getWeatherMessage();

        //readFromAssets();
       // Log.i("Infor", "获取城市天气出错，当前获取的城市是:"+today.temper);

    }

    /**
     * 通过网络获取当前位置信息
     * */
    public boolean getLocalMessage()
    {
        String jsonText = httpGetString.getString(httpCity);
        if(jsonText==null)
            return false;
        int begin = jsonText.toString().indexOf('{');       //获取字符串中第一个‘{’的位置
        int end = jsonText.toString().lastIndexOf('}'); //获取最后一个‘}’的位置

        String jsonStr = jsonText.toString().substring(begin,end+1);    //提取json数据
        if(analysisJsonPosition(jsonStr)==false)      //解析json数据
            return false;
        readFromAssets(); //转换为城市ID
        Log.i("Infor", "readFromAssets" );
        return true;
    }

    /**
     * 获取所在地的天气信息*/
    public boolean getWeatherMessage2() {
        if (localCity == null) {
            return false;
        }
       // getWeatherMessage2();
        //通过网络获取天气信息的xml文件
        String jsonText = httpGetString.getString(httpWeather2 + localCityID+".html");

        if (jsonText == null)
            return false;
        Log.i("Infor", ": " + jsonText);

        int begin = jsonText.toString().indexOf('{');       //获取字符串中第一个‘{’的位置
        int end = jsonText.toString().lastIndexOf('}'); //获取最后一个‘}’的位置

        String jsonStr = jsonText.toString().substring(begin,end+1);    //提取json数据
        analysisJsonWeather(jsonStr);      //解析json数据

        return false;
    }
    /**
     * 获取所在地的天气信息*/
    public boolean getWeatherMessage()
    {
        if( localCity == null )
        {
            if(getLocalMessage()==false)
            {
                return false;
            }
        }
        getWeatherMessage2();
        //通过网络获取天气信息的xml文件
        String xmlText = httpGetString.getString(httpWeather+localCity);

        if(xmlText==null)
            return false;

        String str=" ";
        ByteArrayInputStream stream = new ByteArrayInputStream(xmlText.getBytes());
        try {
            XmlPullParserFactory pullFactory=XmlPullParserFactory.newInstance();
            XmlPullParser pullParser=pullFactory.newPullParser();
            pullParser.setInput(stream,"UTF-8");
            int entype=pullParser.getEventType();

            while (entype!=XmlPullParser.END_DOCUMENT) {
                String startTag=null;
                String textData=null;
                switch(entype) {
                    case XmlPullParser.START_DOCUMENT:   break;
                    case XmlPullParser.START_TAG:
                        String name = pullParser.getName();
                        if (name.equalsIgnoreCase("city")) {
                            String xmlCity = pullParser.nextText();
                            if (xmlCity.equalsIgnoreCase(localCity) == false)    // 如果后面是Text元素,即返回它的值
                            {
                                Log.i("Infor", "获取城市天气出错，当前获取的城市是:" + xmlCity);
                                return false;
                            }
                        } else if (name.equalsIgnoreCase("wendu")) {
                            today.temper = Integer.parseInt(pullParser.nextText());
                        } else if (name.equalsIgnoreCase("shidu")) {
                            /* 删除非数字部分 */
                            String num = pullParser.nextText().replaceAll("[^-+.\\d]", "");
                            today.humidity = Integer.parseInt(num);


                        } else if (name.equalsIgnoreCase("fengxiang")) {
                            today.windDir = pullParser.nextText();
                        } else if (name.equalsIgnoreCase("updatetime")){
                            today.updateTime = pullParser.nextText();
                        }else if (name.equalsIgnoreCase("yesterday")) {

                            int begin = xmlText.toString().indexOf('!');
                            String fengliString = xmlText.substring(begin+6,begin+16);
                            begin = fengliString.toString().indexOf('[');
                            int end = fengliString.toString().indexOf(']');
                            today.windpower = fengliString.substring(begin+1,end);
                            Log.i("Infor", "风力:" + today.windpower);
                            return true;
                        }
                            break;
                    case XmlPullParser.END_DOCUMENT:    break;
                    case XmlPullParser.END_TAG:         pullParser.getName(); break;
                }
                entype=pullParser.next();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i("Infor", "获得当前位置："+str);
        return true;
    }

    /**
     * 通过城市名称获取城市ID代码
     */
    private boolean readFromAssets() {
        if(localCity==null)
            return false;
        InputStream inputStream = null;
        try {

            inputStream = thisApp.getAssets().open("CityId.txt");
            InputStreamReader isr = new InputStreamReader(inputStream,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                if(strLine.indexOf(localCity)!=-1)
                {
                    localCityID = strLine.replaceAll("[^\\d]","");
                    Log.i("Infor",localCityID);
                    break;
                }
            }
            br.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    private boolean  analysisJsonPosition( String jsonString )
    {
        try {
            JSONTokener  jsonParser = new JSONTokener( jsonString );
            JSONObject json = (JSONObject)jsonParser.nextValue();

            localProvince = json.getString("province");

            //字符编码转换 GBK 转 UTF-8
            localCity = new String(json.getString("city").getBytes("UTF-8"),"UTF-8");

            Log.i("Infor", "获得当前位置："+localProvince+localCity);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(localCity.length()<2)
            return false;
        return true;

    }


    private void  analysisJsonWeather( String jsonString )
    {
        try {
            JSONTokener  jsonParser = new JSONTokener( jsonString );
            JSONObject json = (JSONObject)jsonParser.nextValue();

            JSONObject jsonWeather = json.getJSONObject("weatherinfo");
            String str = jsonWeather.getString("city");
            if(str.equals(localCity)==false)
            {
                return;
            }
            str = jsonWeather.getString("temp1");
          //  today.temperMin= Integer.parseInt(str.replaceAll("[^-+.\\d]", ""));
            str = jsonWeather.getString("temp2");
          //  today.temperMax= Integer.parseInt(str.replaceAll("[^-+.\\d]", ""));
          //  Log.i("Infor", "max min："+today.temperMax+" "+today.temperMin);

            today.type = jsonWeather.getString("weather");
           // Log.i("Infor", "type："+today.type);

            today.pic = jsonWeather.getString("img2").replace(".gif","");
            Log.i("Infor", "type："+today.pic);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
