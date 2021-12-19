package com.gq.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.gq.music.R
import com.gq.music.util.Util
import com.gq.video.util.UtilScreen


class ActivityWebDouMusic : AppCompatActivity() {
  
  var webView: WebView? = null
  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_web_dou_music)
    Util.tranStatusBar(this)
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    webView = findViewById<WebView>(R.id.webview)
    webView?.loadUrl("file:///android_asset/musicDou.html")
    webView?.webViewClient = object : WebViewClient() {}
    val webSettings = webView?.settings
    webSettings?.setJavaScriptEnabled(true)
    webSettings?.setAllowUniversalAccessFromFileURLs(true)
    webSettings?.setMediaPlaybackRequiresUserGesture(false)
    
    webView?.addJavascriptInterface(JSCall(UtilScreen(this).sbH),"android")
  }
  
  override fun onDestroy() {
    val viewGroup = webView?.parent as ViewGroup
    viewGroup.removeAllViews()
    webView?.removeAllViews()
    webView?.destroy()
    webView = null
    super.onDestroy()
  }
  inner class JSCall(var sbH:Int){
    @JavascriptInterface
    fun getH():Int {
      return sbH
    }
  }
}