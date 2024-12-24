package com.example.u.ui.view

import androidx.recyclerview.widget.RecyclerView

class WrapContentLayoutManager : RecyclerView.LayoutManager() {

    private var totalHeight = 0  // 总高度

    fun getTotalHeight(): Int {
        return totalHeight
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0 || state.isPreLayout) return

        detachAndScrapAttachedViews(recycler)
        totalHeight = 0

        val parentWidth = width
        var xOffset = 0
        var yOffset = 0
        var rowHeight = 0

        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)

            val childWidth = getDecoratedMeasuredWidth(view)
            val childHeight = getDecoratedMeasuredHeight(view)

            // 如果超出父控件宽度，换行
            if (xOffset + childWidth > parentWidth) {
                xOffset = 0
                yOffset += rowHeight
                rowHeight = 0
            }

            // 布局子视图
            layoutDecorated(
                view,
                xOffset,
                yOffset - verticalOffset,
                xOffset + childWidth,
                yOffset + childHeight - verticalOffset
            )

            xOffset += childWidth
            rowHeight = maxOf(rowHeight, childHeight)
        }

        totalHeight = yOffset + rowHeight
    }

    private var verticalOffset = 0

    override fun canScrollVertically(): Boolean = true

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        // 计算滚动范围
        val delta = when {
            verticalOffset + dy < 0 -> -verticalOffset // 防止滚动到顶部超出范围
            verticalOffset + dy > totalHeight - height -> totalHeight - height - verticalOffset // 防止滚动到底部超出范围
            else -> dy
        }
        // 更新滚动偏移量
        verticalOffset += delta
        // 移动子视图
        offsetChildrenVertical(-delta)

        return delta
    }
}


