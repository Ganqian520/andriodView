//package com.example.short_song.util;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.res.AssetManager;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//
//import com.yanzhenjie.andserver.AndServer;
//import com.yanzhenjie.andserver.Server;
//import com.yanzhenjie.andserver.framework.website.AssetsWebsite;
//
//public class ServiceAndServer extends Service {
//
//  private AndServer andServer;
//  private Server mServer;
//  private AssetManager mAssetManager;
//
//  @Override
//  public void onCreate() {
//    mAssetManager = getAssets();
//    andServer = new AndServer.webServer(this)
//      .port(8080)
//      .website(new AssetsWebsite())
//      .timeout(10*1000)
//      .registerHandler("music",new MusicHandler())
//      .build();
//    mServer = andServer.createServer();
//    mServer.start();
//    Log.i("httpserver","服务已经启动");
//  }
//
//  @Nullable
//  @Override
//  public IBinder onBind(Intent intent) {
//    return null;
//  }
//
//  public void startServer(){
//    mServer.start();
//  }
//
//  public void stopServer(){
//    mServer.stop();
//  }
//
//  public boolean isRunning(){
//    return mServer.isRunning();
//  }
//}
