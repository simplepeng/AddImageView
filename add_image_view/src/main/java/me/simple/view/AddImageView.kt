package me.simple.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    companion object {
        const val VIEW_TYPE_IMAGE_ITEM = 1
        const val VIEW_TYPE_ADD_ITEM = 2
    }

    private val mItems = mutableListOf<String>()
    private val mAdapter = InnerAdapter()

    private var mItemViewDelegate: ItemViewDelegate? = InnerItemViewDelegate()
    private var mAddViewDelegate: AddViewDelegate? = InnerAddViewDelegate()

    /**总共的itemCount*/
    var maxCount = Int.MAX_VALUE
        set(value) {
            adapter?.notifyDataSetChanged()
            field = value
        }

    /**一排的itemCount*/
    var spanCount = 3
        set(value) {
            layoutManager = GridLayoutManager(context, value)
            field = value
        }

    init {
        this.layoutManager = GridLayoutManager(context, spanCount)
        this.adapter = mAdapter
    }

    /**
     * 设置数据
     */
    fun setItems(paths: List<String>) {
        mItems.clear()
        mItems.addAll(paths)
        adapter?.notifyDataSetChanged()
    }

    /**
     * 添加一个item
     */
    fun addItem(path: String) {
        mItems.add(path)
        adapter?.notifyItemInserted(mItems.lastIndex)
    }

    /**
     * 添加一序列的item
     */
    fun addItem(paths: List<String>) {
        mItems.addAll(paths)
        adapter?.notifyItemRangeInserted(mItems.size - paths.size, paths.size)
    }

    /**
     * 删除一个item
     */
    fun removeItem(path: String) {
        val index = mItems.indexOf(path)
        if (index == -1) return
        mItems.removeAt(index)
        adapter?.notifyItemRemoved(index)
    }

    /**
     * 删除一个item
     */
    fun removeItem(position: Int) {
        mItems.removeAt(position)
        adapter?.notifyItemRemoved(position)
    }

    /**
     * 获取数据集
     */
    fun getItems() = mItems

    /**
     *
     */
    fun registerItemViewDelegate(delegate: ItemViewDelegate) {
        this.mItemViewDelegate = delegate
        adapter?.notifyDataSetChanged()
    }

    /**
     *
     */
    fun registerAddViewDelegate(delegate: AddViewDelegate) {
        this.mAddViewDelegate = delegate
        adapter?.notifyDataSetChanged()
    }

    /**
     *
     */
    internal inner class InnerAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount() = if (mItems.size >= maxCount)
            maxCount
        else
            mItems.size + 1

        override fun getItemViewType(position: Int): Int {
            return if (position == itemCount - 1 && mItems.size < maxCount) {
                VIEW_TYPE_ADD_ITEM
            } else {
                VIEW_TYPE_IMAGE_ITEM
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val viewDelegate = if (viewType == VIEW_TYPE_ADD_ITEM)
                mAddViewDelegate!!
            else
                mItemViewDelegate!!
            return viewDelegate.onCreateViewHolder(parent)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            val viewType = getItemViewType(position)
            val viewDelegate = if (viewType == VIEW_TYPE_ADD_ITEM)
                mAddViewDelegate!!
            else
                mItemViewDelegate!!
            viewDelegate.onBindViewHolder(holder, position, this@AddImageView)
        }
    }

    interface InnerViewDelegate {
        fun onCreateViewHolder(parent: ViewGroup): ViewHolder
        fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
            addImageView: AddImageView
        )
    }

    abstract class ItemViewDelegate : InnerViewDelegate

    abstract class AddViewDelegate : InnerViewDelegate
}