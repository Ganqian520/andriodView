package com.gq.music.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gq.music.R;
import com.gq.music.event.EventLogin;
import com.gq.music.util.Data;
import com.gq.music.util.SPutil;
import com.gq.music.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ActivityLogin extends AppCompatActivity {

  private String phone = "19981490817";
  private String password = "511623aA";
  private SPutil sp;
  private Activity activity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    this.activity = this;
    transparent();
    sp = new SPutil(this);
    initBinding();
  }

  private void initBinding(){
    EditText et_phone = findViewById(R.id.et_phone);
    EditText et_password = findViewById(R.id.et_password);
    TextView bt_login = findViewById(R.id.bt_login);
    bt_login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String url = Data.baseUrl+"/login/cellphone?phone=" + phone + "&password=" + password;
        Callback cb = new Callback() {
          @Override
          public void onFailure(@NotNull Call call, @NotNull IOException e) {
            System.out.println(e);
            System.out.println("登陆失败");
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(getBaseContext(),e.toString(),Toast.LENGTH_LONG);
              }
            });
          }

          @Override
          public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            String str = response.body().string();
            JSONObject res = JSON.parseObject(str);
            DataLogin dataLogin = new DataLogin();
            dataLogin.uid = res.getJSONObject("account").getString("id");
            dataLogin.nickName = res.getJSONObject("profile").getString("nickname");
            dataLogin.token = res.getString("token");
            dataLogin.cookie = res.getString("cookie");
            Data.dataLogin = dataLogin;
            String str1 = JSON.toJSONString(dataLogin);
            sp.putString("dataLogin",str1);
            System.out.println("登录成功，全局datalogin已赋值");
            System.out.println(dataLogin.cookie);
            EventBus.getDefault().post(new EventLogin(true));
            finish();
          }
        };
        Util.get(url,cb);
      }
    });
    et_phone.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        phone = s.toString();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
    et_password.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        password = s.toString();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
  }

  void transparent(){
    Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.setStatusBarColor(Color.TRANSPARENT);
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
  }
}