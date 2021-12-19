package com.gq.music;

import android.app.Application;
import android.content.Context;

import com.aitangba.swipeback.ActivityLifecycleHelper;
import com.danikula.videocache.HttpProxyCacheServer;
import com.gq.music.service.PlayerService;

public class MyApplication extends Application {
  private HttpProxyCacheServer proxy;
  public PlayerService.MusicControl control;
  public static Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    MyApplication.context = getApplicationContext();
    registerActivityLifecycleCallbacks(ActivityLifecycleHelper.build());  //滑动返回
  }

  public static HttpProxyCacheServer getProxy(Context context) {  //获取代理
    MyApplication app = (MyApplication) context.getApplicationContext();
    return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
  }

  private HttpProxyCacheServer newProxy() { //返回代理
    return new HttpProxyCacheServer
      .Builder(this)
      .maxCacheSize(1024*1024*1024)   //设置最大缓存容量，自动清理老文件
      .build();
  }
}
