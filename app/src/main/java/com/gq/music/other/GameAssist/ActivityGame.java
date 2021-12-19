package com.gq.music.other.GameAssist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.util.SPutil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class ActivityGame extends AppCompatActivity{
  private Activity activity;
  private TextView tv_add;
  private ListView lv_hero;
  private ServiceFloating.Control control;
  private SPutil spUtil;
  private ArrayList<Assist> list = new ArrayList<Assist>();
  private MyBroadcastReceiver receiver;
  private int cursor = 0;
  private AdapterList adapterList;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_game);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    activity = this;
    spUtil = new SPutil(this);
    getGD();
    getScreen();
    try{
      initService();
    }catch(Exception e){System.out.println("异常："+e);}
    initReceiver();
    EventBus.getDefault().register(this); //订阅消息
  }

  @Override
  public void onConfigurationChanged(@NonNull Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    System.out.println("change");
  }

  //订阅消息 名字被修改
  @Subscribe
  public void onMessageEvent(String message) {
    list.get(cursor).name = message;
    adapterList.notifyDataSetChanged();
    String str = JSON.toJSONString(list);
    spUtil.putString("assistList",str);
  }

  void initService(){
    ServiceConnection conn = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName name, IBinder service) {
        control = (ServiceFloating.Control) service;
        initBinding(); //control的获取是异步的
      }
      @Override
      public void onServiceDisconnected(ComponentName name) {

      }
    };
    Intent intent = new Intent(this, ServiceFloating.class);
    bindService(intent,conn,BIND_AUTO_CREATE);
  }

  private void initReceiver(){
    receiver = new MyBroadcastReceiver();
    IntentFilter filter = new IntentFilter();
    filter.addAction("location.reports");
    registerReceiver(receiver,filter);
  }

  private void initBinding() {
    lv_hero = findViewById(R.id.lv_hero);
    adapterList = new AdapterList(getBaseContext(),this, list, control, new AdapterList.MyInterface() {
      @Override
      public void callback(int position) {
        cursor = position;
        GD.assist = list.get(cursor);
      }
    });
    lv_hero.setAdapter(adapterList);
    tv_add = findViewById(R.id.tv_add);
    tv_add.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          Assist assist = list.get(0).clone();
          assist.name = "新加英雄";
          list.add(assist);
          adapterList.notifyDataSetChanged();
        } catch (CloneNotSupportedException e) {
          e.printStackTrace();
        }
      }
    });
    TextView tv_eidt = findViewById(R.id.tv_edit);
    tv_eidt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        InputDialog dialog = new InputDialog(activity,2);
        dialog.setCancelable(false);
        dialog.show();
      }
    });
  }
//获取数据
  public void getGD(){
    String str = spUtil.getString("assistList");
    if(str==""){
      Assist assist = new Assist((int) (GD.w/2), (int) (GD.w/2),0,0);
      assist.name = "未命名";
      list.add(assist);
    }else{
      JSONArray jsArr = JSONArray.parseArray(str);
      for(int i=0;i<jsArr.size();i++){
        Assist assist = JSONObject.toJavaObject((JSON) jsArr.get(i),Assist.class);
        list.add(assist);
      }
    }
    GD.assist = list.get(0);
    GD.textShow = spUtil.getString("textShow");
  }
  class MyBroadcastReceiver extends BroadcastReceiver{
    //保存
    @Override
    public void onReceive(Context context, Intent intent) {
      if(intent.getAction().equals("location.reports")){   //悬浮窗点击了保存
        list.set(cursor,GD.assist);
        String str = JSON.toJSONString(list);
        spUtil.putString("assistList",str);
      }
    }
  }

  void getScreen(){
    int statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    GD.sbH = statusBarHeight;
    GD.w = displayMetrics.heightPixels;
    GD.h = displayMetrics.widthPixels;
//    System.out.println(GD.sbH+"  "+GD.h);
  }

  @Override
  protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    unregisterReceiver(receiver);
    super.onDestroy();
  }
}
//    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
//  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); //随重力旋转