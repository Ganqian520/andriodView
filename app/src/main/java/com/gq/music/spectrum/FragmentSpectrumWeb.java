package com.gq.music.spectrum;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gq.music.R;

public class FragmentSpectrumWeb extends Fragment {
  private WebView webView;
  private WebSettings webSettings;
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_spectrum_web,container,false);
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  public void onStart() {
    super.onStart();
    webView = getView().findViewById(R.id.wbv);
    webView.setBackgroundColor(0); // 设置背景色
    webView.addJavascriptInterface(new JScall(), "android");
    webView.loadUrl("file:///android_asset/spectrum.html");
    webView.setWebViewClient(new WebViewClient(){
    });
    webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setAllowFileAccess(true);
    webSettings.setMediaPlaybackRequiresUserGesture(false);

    getView().findViewById(R.id.tv_play).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        System.out.println(""+"web 点击");
        webView.loadUrl("javascript:javaCall1()");
      }
    });
  }
  class JScall{
    @JavascriptInterface
    public void get(String p){
      System.out.println("JScall****"+p);
    }
  }
}
