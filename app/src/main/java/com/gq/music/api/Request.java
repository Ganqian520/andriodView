package com.gq.music.api;

import com.alibaba.fastjson.JSON;
import com.gq.music.util.Data;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Request {
  //get请求
  public static void get(String url, Callback cb){
    OkHttpClient okHttpClient= new OkHttpClient.Builder()
//      .addNetworkInterceptor(new MyIntercepter())
      .build();
    okhttp3.Request request= new okhttp3.Request.Builder().get()
      .addHeader("token", Data.dataLogin.token)
      .addHeader("cookie",Data.dataLogin.cookie)
      .url(url).build();
    Call call = okHttpClient.newCall(request);
    call.enqueue(cb);
  }

  //云函数post请求
  public static void myPost(Map map, Callback cb){
    String url = "https://ca448d14-fda5-4d8f-9279-3f4896d8f854.bspapp.com/index";
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(map));
    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
    okhttp3.Request request= new okhttp3.Request.Builder().post(body).url(url).addHeader("content-type", "application/json").build();
    Call call = okHttpClient.newCall(request);
    call.enqueue(cb);
  }
}
