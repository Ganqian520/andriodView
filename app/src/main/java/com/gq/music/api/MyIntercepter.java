package com.gq.music.api;

import com.gq.music.util.Data;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MyIntercepter implements Interceptor {
  @NotNull
  @Override
  public Response intercept(@NotNull Chain chain) throws IOException {
    Request request =  chain
      .request()
      .newBuilder()
      .addHeader("token", Data.dataLogin.token)
      .addHeader("cookie",Data.dataLogin.cookie)
      .build();
    return chain.proceed(request);
  }
}
