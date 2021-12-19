package com.gq.music.other.GameAssist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CanvasAssist extends View {
  Paint paint;
  float w = (float) (GD.h+GD.sbH);
  float h = (float) (GD.w);
  public CanvasAssist(Context context) {
    super(context);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paint = new Paint();
    paint.setAntiAlias(true);
    paint.setColor(Color.argb(255,255,255,0));
    paint.setStyle(Paint.Style.STROKE);//设置画笔为画线条模式
    paint.setStrokeWidth(2);//设置线宽
    //第一个椭圆
    RectF rectF = new RectF();
    rectF.left = (float) (w/2-GD.assist.w);
    rectF.top = (float) (h/2-GD.assist.h+GD.assist.y);
    rectF.right = (float) (w/2+GD.assist.w);
    rectF.bottom = (float) (h/2+GD.assist.h+GD.assist.y);
    canvas.drawOval(rectF, paint);
    //第二个椭圆
    RectF rectF2 = new RectF();
    rectF2.left = (float) (w/2-GD.assist.w2);
    rectF2.top = (float) (h/2-GD.assist.w2*((float)GD.assist.h/GD.assist.w)+GD.assist.y);
    rectF2.right = (float) (w/2+GD.assist.w2);
    rectF2.bottom = (float) (h/2+GD.assist.w2*((float)GD.assist.h/GD.assist.w)+GD.assist.y);
    if(GD.assist.w2!=0) canvas.drawOval(rectF2, paint);
  }
  public void redraw(){
    invalidate();
  }
  public CanvasAssist(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }
}
