package com.lmissw.mydesignedapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ZhouWangsheng on 2018/1/11.
 */

public class setActivity extends AppCompatActivity {

    private InputMethodManager manager;

    private EditText allPower = null;
    private EditText allPower1 = null;
    private EditText allPower2 = null;
    private EditText allPower3 = null;
    private EditText blockTime = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  /* 全屏显示 */
        setContentView(R.layout.layout_set);
        setActionBar();
        //PutKeyAndValue("City","北京");
        //Log.i("Infor",getValueFromKey("City"));

        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        allPower = findViewById(R.id.powerAllMax);
        allPower1 = findViewById(R.id.power1Max);
        allPower2 = findViewById(R.id.power2Max);
        allPower3 = findViewById(R.id.power3Max);
        blockTime = findViewById(R.id.blockOutTime);


        allPower.setText(getValueFromKey("allPowerMax"));
        allPower1.setText(getValueFromKey("Power1Max"));
        allPower2.setText(getValueFromKey("Power2Max"));
        allPower3.setText(getValueFromKey("Power3Max"));
        blockTime.setText(getValueFromKey("blockOutTime"));

    }

    private void setActionBar() {
        ActionBar actionBar=this.getSupportActionBar();
        // 显示返回按钮
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 去掉logo图标
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setTitle("设置");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:   //返回键的id
                this.finish();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    private String getValueFromKey( String key )
    {
        SharedPreferences userSettings = getSharedPreferences("setting", 0);
        return userSettings.getString(key,"default");
    }

    private void PutKeyAndValue( String key, String Value )
    {
        SharedPreferences userSettings= getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.remove(key);
        editor.putString(key,Value);
        editor.commit();
    }


    public void cancelClick( View view ) {
        this.finish();
    }

    public void okClick( View view ) {
        if(allPower.getText().length()==0)
        {
            Toast.makeText(setActivity.this, "请输入参数", Toast.LENGTH_LONG).show();
            return;
        }else if(allPower1.getText().length()==0)
        {
            Toast.makeText(setActivity.this, "请输入参数", Toast.LENGTH_LONG).show();
            return;
        }else if(allPower2.getText().length()==0)
        {
            Toast.makeText(setActivity.this, "请输入参数", Toast.LENGTH_LONG).show();
            return;
        }else if(allPower3.getText().length()==0)
        {
            Toast.makeText(setActivity.this, "请输入参数", Toast.LENGTH_LONG).show();
            return;
        }else if(blockTime.getText().length()==0)
        {
            Toast.makeText(setActivity.this, "请输入参数", Toast.LENGTH_LONG).show();
            return;
        }
        int allMax=Integer.parseInt(allPower.getText().toString());
        int p1 = Integer.parseInt(allPower1.getText().toString());
        int p2 = Integer.parseInt(allPower2.getText().toString());
        int p3 = Integer.parseInt(allPower3.getText().toString());
        if( allMax>p1 && allMax>p2 && allMax>p3 )
        {
            if( Integer.parseInt(blockTime.getText().toString())<5 )
            {
                Toast.makeText(setActivity.this, "请检查参数", Toast.LENGTH_LONG).show();
                return ;
            }


            PutKeyAndValue("allPowerMax",allPower.getText().toString());
            PutKeyAndValue("Power1Max",allPower1.getText().toString());
            PutKeyAndValue("Power2Max",allPower2.getText().toString());
            PutKeyAndValue("Power3Max",allPower3.getText().toString());
            PutKeyAndValue("blockOutTime",blockTime.getText().toString());
            Toast.makeText(setActivity.this, "参数保存成功", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(setActivity.this, "请检查参数", Toast.LENGTH_LONG).show();
            return;
        }

        return;
    }
}
