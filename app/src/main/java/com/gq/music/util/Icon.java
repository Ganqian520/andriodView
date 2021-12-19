package com.gq.music.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class Icon extends TextView {

  public Icon(Context context) {
    super(context);
    init(context);
  }

  public Icon(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public Icon(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context);
  }

  private void init(Context context) {
    Typeface font = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
    this.setTypeface(font);
  }

}
