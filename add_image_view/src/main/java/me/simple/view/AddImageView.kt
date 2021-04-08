package me.simple.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
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

    var maxCount = Int.MAX_VALUE
    var spanCount = 3

    private val mItems = mutableListOf<Any>()
    private val mAdapter = Adapter()

    private val mItemViewBinders = mutableMapOf<Int,ItemViewBinder<*, *>>()

    init {
        mItems.add(AddItem())
        layoutManager = GridLayoutManager(context, spanCount)
        adapter = mAdapter
    }

    fun <T> register(itemViewBinder: ItemViewBinder<T, *>) {
        if (itemViewBinder is ImageItemViewBinder) {
            mItemViewBinders.put(VIEW_TYPE_IMAGE_ITEM, itemViewBinder)
        }
        if (itemViewBinder is AddItemViewBinder) {
            mItemViewBinders.put(VIEW_TYPE_ADD_ITEM, itemViewBinder)
        }
    }

    fun setItems(paths: List<String>) {
        val index = mItems.size - 1
        mItems.addAll(index, paths)
        mAdapter.notifyDataSetChanged()
    }

    fun addItem(path: String) {
        val index = mItems.size - 1
        mItems.add(index, path)
        mAdapter.notifyItemInserted(index)
    }

    fun addItem(paths: List<String>) {
        val index = mItems.size - 1
        mItems.addAll(index, paths)
        mAdapter.notifyItemRangeInserted(index, paths.size)
    }

    fun removeItem(path: String) {
        val index = mItems.indexOf(path)
        mItems.removeAt(index)
        mAdapter.notifyItemRemoved(index)
    }

    internal inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount() = mItems.size

        override fun getItemViewType(position: Int): Int {
            if (mItems.isEmpty()) return super.getItemViewType(position)

            if (mItems[position] is AddItem) {
                return VIEW_TYPE_ADD_ITEM
            }

            return VIEW_TYPE_IMAGE_ITEM
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return mItemViewBinders[viewType]!!.onCreateViewHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            val item = mItems[position]
            val viewType = getItemViewType(position)
            val itemViewBinder = mItemViewBinders[viewType]!! as ItemViewBinder<Any, ViewHolder>


            itemViewBinder.onBindViewHolder(this@AddImageView, holder, item)
        }
    }

    abstract class ItemViewBinder<T, VH : ViewHolder> {
        abstract fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup): VH

        abstract fun onBindViewHolder(addImageView: AddImageView, holder: VH, item: T)
    }

    abstract class ImageItemViewBinder<VH : ViewHolder> : ItemViewBinder<String, VH>()

    abstract class AddItemViewBinder<VH : ViewHolder> : ItemViewBinder<AddItem, VH>()
}