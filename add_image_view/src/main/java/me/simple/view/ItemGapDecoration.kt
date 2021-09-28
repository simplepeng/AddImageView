package me.simple.view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

open class ItemGapDecoration(
    private val itemGap: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
//        val layoutManager = parent.layoutManager as? GridLayoutManager ?: return
//        val adapter = parent.adapter ?: return
//        val position = parent.getChildAdapterPosition(view)
//        val spanCount = layoutManager.spanCount


        outRect.set(itemGap, itemGap, itemGap, itemGap)

//        var left = 0
//        var top = 0
//        var right = 0
//        var bottom = 0
//
//        when {
//            position % spanCount == 0 -> {//left
//                left = 0
//                right = itemGap
//            }
//            (position + 1) % spanCount == 0 -> {//right
//                left = itemGap
//                right = itemGap
//            }
//            else -> {//center
//                left = itemGap / 2
//                right = 0
//            }
//        }
//
//        bottom = itemGap
//
//        outRect.set(left, top, right, bottom)

//        val column = position % spanCount
//        outRect.left = column * itemGap / spanCount
//        outRect.right = itemGap - (column + 1) * itemGap / spanCount
//        outRect.bottom = itemGap
    }
}