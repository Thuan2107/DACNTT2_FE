package com.example.chatapplication.model.entity

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class MyItemDecoration(private var overlapValue: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position: Int = parent.getChildAdapterPosition(view)

        if (position == state.itemCount - 1)
            outRect.setEmpty()
        else
            outRect.set(0, 0, overlapValue, 0)  // args is : left,top,right,bottom

    }

}