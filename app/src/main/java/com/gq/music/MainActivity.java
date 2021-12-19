package com.gq.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.gq.music.Login.DataLogin;
import com.gq.music.components.AdapterVp2Main;
import com.gq.music.control.FragmentControl;
import com.gq.music.components.FragmentDrawer;
import com.gq.music.event.EventAddCustom;
import com.gq.music.search.ActivitySearch;
import com.gq.music.util.Data;
import com.gq.music.util.Icon;
//import com.example.short_song.util.ServiceAndServer;
import com.gq.music.util.MyAnimation;
import com.gq.music.util.MyDialog;
import com.gq.music.util.SPutil;
import com.gq.music.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.gq.music.MyApplication.getProxy;


public class MainActivity extends AppCompatActivity {

  private RelativeLayout rel_main;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    EventBus.getDefault().register(this);
    initFragment();
    getScreen();
    Util.tranStatusBar(this);
    initBinding();
    Util.getPermisson(this,getBaseContext());
    initGlobalData();
  }

  void initGlobalData(){
    SPutil sp = new SPutil(this);
    Data.proxy = getProxy(this);  //播放器代理
    String str = sp.getString("dataLogin");
    if(!str.equals("")){
      DataLogin dataLogin = JSONObject.toJavaObject(JSONObject.parseObject(str),DataLogin.class);
      Data.dataLogin = dataLogin;
    }
  }

  void initFragment(){
    getSupportFragmentManager().beginTransaction().add(R.id.fg_control, new FragmentControl(),"fg_control").commitAllowingStateLoss();
    getSupportFragmentManager().beginTransaction().add(R.id.fg_drawer, new FragmentDrawer(),"fg_drawer").commitAllowingStateLoss();
    ViewPager2 vp2 = findViewById(R.id.vp2);
    FragmentStateAdapter adapterVp2Main = new AdapterVp2Main(this);
    vp2.setAdapter(adapterVp2Main);
    vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @Override
      public void onPageSelected(int position) {
        super.onPageSelected(position);
      }
    });
  }

  void initBinding(){
    rel_main = findViewById(R.id.rel_main);
    Icon icon_add1 = findViewById(R.id.icon_add1);
    icon_add1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MyDialog myDialog = new MyDialog(MainActivity.this);
        myDialog.setCancelable(false);
        myDialog.show();
      }
    });
  }
  public void goSearch(View v){
    startActivity(new Intent(this, ActivitySearch.class));
  }

  @Subscribe  //监听自定义队列的添加
  public void onAddCustom(EventAddCustom event){
    Icon ic_temp = new Icon(getBaseContext());
    ic_temp.setText(R.string.add);
    ic_temp.setTextColor(Color.WHITE);
    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    lp.leftMargin = event.location[0];
    lp.topMargin = event.location[1];
    rel_main.addView(ic_temp,lp);
    MyAnimation.aniAdd(ic_temp,rel_main);
  }

  void getScreen(){
    int statusBarHeight = getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android"));
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    Data.sbH = statusBarHeight;
    Data.h = displayMetrics.heightPixels;
    Data.w = displayMetrics.widthPixels;
  }


}