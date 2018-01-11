package com.lmissw.mydesignedapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.format.Time;
import android.util.Log;

import android.view.View;
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
    private ImageView ivStatus = null;

    private int elcStatus = 0;
    public final class Status {
        /* 断电 */
        public final static int blackOut = 1;
        /* 应急灯 */
        public final static int light = 2;
        /* 无人 */
        public final static int noPerson = 4;
        /* 停电 */
        public  final static int stopElc = 8;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  /* 全屏显示 */
        setContentView(R.layout.activity_main);
        tvDate = findViewById(R.id.date);
        tvClock = findViewById(R.id.clock);
        tvWeather = findViewById(R.id.weather);
        ivWeather = findViewById(R.id.imageWeather);
        ivStatus = findViewById(R.id.eStatus);
        updateWeather();    //启动首先更新天气

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
                    Thread.sleep(1000);

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
                    String nowTime = String.format("%02d:",t.hour)+String.format("%02d",t.minute);
                    tvClock.setText(nowTime);
                    updateDate();

                    if( t.minute%20==0 && t.second>56 )
                        updateWeather();


                    break;
                default:
                    break;
            }
        }
    };




    public void normalClick( View view )
    {
        if( (elcStatus & Status.stopElc) != 0 )
        {
            Log.i("Infor","已经停电了，无法进入normal");
            return;
        }
        elcStatus &= ~(Status.blackOut);
        elcStatus &= ~(Status.noPerson);
        updateStatus();
    }

    public void noPersonClick( View view ) {
        if(0==(elcStatus & Status.noPerson) )
            elcStatus |= Status.noPerson;
        else
            elcStatus &= ~Status.noPerson;

        updateStatus();
    }

    public void lightClick( View view ) {
        if(0==(elcStatus & Status.light) )
            elcStatus |= Status.light;
        else
            elcStatus &= ~Status.light;

        updateStatus();
    }

    public void updateStatus()
    {
        if( (elcStatus & Status.noPerson) != 0 )
        {
         //   Log.i("Infor","无人模式");
        }

        if( (elcStatus & Status.light) != 0 ) {
            Log.i("Infor","开灯");
        }
        else
        {
            Log.i("Infor","关灯");
        }

        if( ((elcStatus & Status.stopElc) != 0) || ((elcStatus & Status.blackOut) != 0)  )
        {

            Resources res = getResources();
            Context ctx=getBaseContext();
            int resId = getResources().getIdentifier("falsex", "drawable", ctx.getPackageName());
            ivStatus.setImageDrawable( res.getDrawable(resId) );

            Log.i("Infor","停电");

        }
        else if( (elcStatus & Status.noPerson) != 0 ) {
            Resources res = getResources();
            Context ctx=getBaseContext();
            int resId = getResources().getIdentifier("noperson", "drawable", ctx.getPackageName());
            ivStatus.setImageDrawable( res.getDrawable(resId) );
            Log.i("Infor","无人模式");
        }else{

            Resources res = getResources();
            Context ctx=getBaseContext();
            int resId = getResources().getIdentifier("truey", "drawable", ctx.getPackageName());
            ivStatus.setImageDrawable( res.getDrawable(resId) );

            Log.i("Infor","正常模式");
        }





    }

    public void blackOutClick( View view ){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */

        if((elcStatus & Status.stopElc) != 0 || ((elcStatus & Status.blackOut) != 0))
        {
            Toast.makeText(MainActivity.this, "电源已经被切断", Toast.LENGTH_LONG).show();
            return;
        }
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MainActivity.this);
       // normalDialog.setIcon(R.drawable.icon_dialog);
        normalDialog.setTitle("即将切断主电源");
        normalDialog.setMessage("切断主电源?");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        elcStatus |= Status.blackOut;
                        elcStatus &= ~(Status.noPerson);
                        Log.i("Infor","blackOutClick");
                        updateStatus();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        // 显示
        normalDialog.show();
    }

}
