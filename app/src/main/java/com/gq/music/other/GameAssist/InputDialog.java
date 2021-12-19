package com.gq.music.other.GameAssist;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.gq.music.R;
import com.gq.music.util.SPutil;

import org.greenrobot.eventbus.EventBus;

public class InputDialog extends Dialog {

  private Button bt_cancel;
  private Button bt_confirm;
  private EditText et_name;
  private int flag;
  private SPutil spUtil;

  public InputDialog(@NonNull Context context,int flag) {
    super(context,R.style.myDialog);
    setContentView(R.layout.layout_input_dialog);
    this.flag = flag;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initBinding();
    spUtil = new SPutil(getContext());
  }

  private void initBinding() {
    final String[] name = {""};
    bt_cancel = findViewById(R.id.bt_cancel);
    bt_confirm = findViewById(R.id.bt_confirm);
    et_name = findViewById(R.id.et_name);
    bt_cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cancel();
      }
    });
    bt_confirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(flag==1){  //改英雄名
          EventBus.getDefault().post(name[0]);
        }else{  //改提示文字
          GD.textShow = name[0];
          spUtil.putString("textShow",name[0]);
        }
        cancel();
      }
    });
    et_name.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        name[0] = s.toString();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
  }

  @Override
  public void show() {
    super.show();
  }

  @Override
  public void cancel() {
    super.cancel();
  }
}
