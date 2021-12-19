package com.gq.music.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.gq.music.api.MyIntercepter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Util {
  //透明化信号栏
  public static void tranStatusBar(Activity activity){
    Window window = activity.getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.setStatusBarColor(Color.TRANSPARENT);
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }
  //时间转换
  public static String transTime(int seconds){
    int minute = (int) ((float)seconds/60);
    int second = seconds%60;
    String str_second = "";
    String str_minute = "";
    if(second>9){
      str_second = ""+second;
    }else{
      str_second = "0"+second;
    }
    if(minute>9) {
      str_minute = "" + minute;
    }else if(minute==0){
      str_minute = "00";
    }else{
      str_minute = "0"+minute;
    }
    return str_minute+":"+str_second;
  }
  //获取权限
  public static void getPermisson(Activity activity,Context context){
    String[] all = {Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.RECORD_AUDIO};
    ArrayList<String> no = new ArrayList<String>();
    for(int i=0;i<all.length;i++){
      int permission = ContextCompat.checkSelfPermission(activity.getApplication(), all[i]);
      if(permission != PackageManager.PERMISSION_GRANTED){
        no.add(all[i]);
      }
    }
    ActivityCompat.requestPermissions(activity,(String[]) no.toArray(new String[0]), 0);
    if( ! Settings.canDrawOverlays(activity)){
//      startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName())), 0);
    }
  }

  //get请求
  public static void get(String url,Callback cb){
    OkHttpClient okHttpClient= new OkHttpClient.Builder().addNetworkInterceptor(new MyIntercepter()).build();
    Request request= new Request.Builder().get().url(url).build();
    Call call = okHttpClient.newCall(request);
    call.enqueue(cb);
  }

  //解析分享链接
  public static void getUrl() {
    String url = "https://ca448d14-fda5-4d8f-9279-3f4896d8f854.bspapp.com/index";
    MediaType mediaType = MediaType.parse("application/json");
    Map map = new HashMap();
    map.put("action","douYinMark");
    JSONObject json = new JSONObject(map);
    RequestBody body = RequestBody.create(mediaType, String.valueOf(json));

    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

    Request request= new Request.Builder().post(body).url(url).addHeader("content-type", "application/json").build();
    Call call = okHttpClient.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        System.out.println("err");
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        String str = response.body().string();
        try {
          JSONObject jsonObj = new JSONObject(str);
          JSONArray jsonArr = (JSONArray) jsonObj.get("data");
          System.out.println();
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
