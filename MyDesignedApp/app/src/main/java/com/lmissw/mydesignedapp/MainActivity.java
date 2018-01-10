package com.lmissw.mydesignedapp;


import android.content.Context;
import android.content.res.Resources;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.format.Time;
import android.util.Log;

import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import android.os.Handler;



public class MainActivity extends AppCompatActivity {
    private int numTemp = 0;

    private ConstraintLayout thisAPP;
    private TextView tvDate=null;
    private TextView tvClock = null;
    private TextView tvWeather = null;
    private ImageView ivWeather = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  /* 全屏显示 */
        setContentView(R.layout.activity_main);
        tvDate = findViewById(R.id.date);
        tvClock = findViewById(R.id.clock);
        tvWeather = findViewById(R.id.weather);
        ivWeather = findViewById(R.id.imageWeather);
        updateWeather();    //启动首先更新天气

        Toast.makeText(MainActivity.this, "你好!", Toast.LENGTH_LONG).show();

        Log.i("Infor","onCreate");

        new TimeThread().start();
        // readFromAssets();
    }

    private void updateWeather()
    {
        getWeather weather = getWeather.getInstace(this);
        weather.getLocalMessage();
        weather.getWeatherMessage();

        String str = weather.getCity() + "市 "+weather.today.type+" "+weather.today.temper+"℃ "+weather.today.windDir+weather.today.windpower;
        tvWeather.setText(str);
        Log.i("Infor","xxxx");
        Log.i("Infor","type:"+weather.today.type);
        Log.i("Infor","temper:"+weather.today.temper);

        Log.i("Infor","humidity:"+weather.today.humidity);
        Log.i("Infor","windpower:"+weather.today.windpower);
        Log.i("Infor","updateTime:"+weather.today.updateTime);

        Resources res = getResources();
        Context ctx=getBaseContext();
        int resId = getResources().getIdentifier(weather.today.pic, "drawable", ctx.getPackageName());
        ivWeather.setImageDrawable( res.getDrawable(resId) );


    }

    private void updateDate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);

        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                str += " 周日";    break;
            case 2:
                str += " 周一";    break;
            case 3:
                str += " 周二";    break;
            case 4:
                str += " 周三";    break;
            case 5:
                str += " 周四";    break;
            case 6:
                str += " 周五";    break;
            case 7:
                str += " 周六";    break;
            default:    break;

        }
        tvDate.setText(str);
    }



    public class TimeThread extends  Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String str=null;
            switch (msg.what){
                case 1:
                    Time t=new Time();
                    t.setToNow(); // 取得系统时间。
                    String nowTime = t.hour+":"+t.minute;
                    tvClock.setText(nowTime);
                    updateDate();

                    if( t.minute==0 && t.second>56 )
                        updateWeather();

                    break;
                default:
                    break;
            }
        }
    };

}
