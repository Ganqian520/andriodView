package com.gq.music.spectrum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ViewCanvas extends View {
  private float[] data;
  private Paint paint;
  private int w = 0;
  private int h = 0;

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if(w==0){
      w = canvas.getWidth();
      h = canvas.getHeight();
    }
    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清空画布
    paint.setAntiAlias(true); //抗锯齿
    paint.setStrokeWidth(5);
    paint.setColor(Color.argb(255,255,255,255));
    if(data != null){
      for(int i=0;i<data.length;i++){
        if (data[0]==0){
          return;
        }
        canvas.drawCircle(((float)w)/data.length*i,h-data[i]*5-3,3,paint);
//        canvas.drawLine(((float)w)/data.length*i,h-0,((float)w)/data.length*i,h-data[i]*20,paint);
      }
    }
  }

  public void myDraw(float[] _data){
    this.data = _data;
    invalidate();
  }

  public ViewCanvas(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    paint = new Paint();
  }
}

