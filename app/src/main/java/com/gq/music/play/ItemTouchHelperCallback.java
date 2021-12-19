package com.gq.music.play;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

  private AdapterCollect adapterCollect;

  public ItemTouchHelperCallback(AdapterCollect adapterCollect) {
    this.adapterCollect = adapterCollect;
  }

  @Override
  public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
    //允许上下的拖动
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    //只允许从右向左侧滑
    int swipeFlags = ItemTouchHelper.LEFT;
    return makeMovementFlags(dragFlags, swipeFlags);
  }

  @Override
  public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
    adapterCollect.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    return true;
  }

  @Override
  public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return false;
  }

  @Override
  public void onChildDraw(Canvas c, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//    if(actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//      //滑动时改变Item的透明度
//      final float alpha = 1 - Math.abs(dX) / (float)viewHolder.itemView.getWidth();
//      viewHolder.itemView.setAlpha(0.5f);
//    }
  }

}
