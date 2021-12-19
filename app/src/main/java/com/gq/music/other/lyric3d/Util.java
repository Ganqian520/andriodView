package com.gq.music.other.lyric3d;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Util {

  //计算大小
  public static float calArea(float n){
    if(n<=0.5){
      return 2*n;
    }else {
      return -2*n+2;
    }
  }

  //根据中心点绘制文字
  public static void drawTextCenter ( int centerX, int centerY, String text,Canvas canvas, Paint paint) {
    float textWidth = paint.measureText(text);
    Paint.FontMetrics fontMetrics = paint.getFontMetrics();
    float baselineY = centerY + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
    canvas.drawText(text, centerX - textWidth / 2, baselineY, paint);
  }

}
