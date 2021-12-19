package com.gq.music.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.view.View;
import android.widget.RelativeLayout;

public class MyAnimation {



  public static void aniRock(View v){
    ObjectAnimator animator = ObjectAnimator.ofFloat(v,"translationX",0,50,-50,0);
    animator.setDuration(500);
    animator.start();
  }

  public static void aniAdd(View view, RelativeLayout view_parent){
    AnimatorSet animatorSet = new AnimatorSet();
    ObjectAnimator animator1 = ObjectAnimator.ofFloat(view,"translationX",0,-1000);
    ObjectAnimator animator2 = ObjectAnimator.ofFloat(view,"translationY",0,1500);
    animator2.setEvaluator(new MyEvaluator());
    animatorSet.playTogether(animator1,animator2);
    animatorSet.setDuration(1000);
    animatorSet.start();
    animatorSet.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {}
      @Override
      public void onAnimationEnd(Animator animation) {
        view_parent.removeView(view);
      }
      @Override
      public void onAnimationCancel(Animator animation) {}
      @Override
      public void onAnimationRepeat(Animator animation) {}
    });
  }

}
class MyEvaluator implements TypeEvaluator {
  @Override
  public Object evaluate(float fraction, Object startValue, Object endValue) {
    return (float) -1500*fraction+0.5*6000*Math.pow(fraction,2) ;
  }
}
