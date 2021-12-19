package com.gq.music.play;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;


@SuppressLint("AppCompatCustomView")
public class TextViewCommentsLoop extends TextView {
  private int currentScrollX = 0;// 当前滚动的位置
  private int textWidth;
  private String text = "";
  private int vWidth = 0;
  private final int speed = 5;
  LinearGradient gradient;
  Paint paint;
  float baseline;
  boolean isStop = true;
  ValueAnimator valueAnimator;

  public TextViewCommentsLoop(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (vWidth==0) {
      init();
    }
    canvas.drawText(text, currentScrollX, baseline, paint);
  }

  void init(){
    vWidth = getWidth();
    currentScrollX = vWidth;
    baseline = getHeight() / 2 + getPaint().getTextSize() / 2 - getPaint().getFontMetrics().descent;
    paint = getPaint();
    gradient = new LinearGradient(0, 0, vWidth, 0,
      new int[]{Color.TRANSPARENT, getCurrentTextColor(),getCurrentTextColor(), Color.TRANSPARENT},
      new float[]{0f, 0.2f,0.8f, 1f},
      Shader.TileMode.CLAMP);
    paint.setShader(gradient);
    valueAnimator = ValueAnimator.ofInt(0,6000000);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        currentScrollX -= speed;// 滚动速度
        if (currentScrollX < 0) {
          if (currentScrollX <= -textWidth) {
            currentScrollX = vWidth;
          }
        }
        invalidate();
      }
    });
    valueAnimator.setDuration(6000000);
    valueAnimator.start();
  }

  public void start(){
    isStop = false;
    valueAnimator.start();
  }
  public void stop(){
    isStop = true;
    valueAnimator.cancel();
  }
  public void setText(String text) {
    this.text = text;
    textWidth = (int) getPaint().measureText(text);
  }
}