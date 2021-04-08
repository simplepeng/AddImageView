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
        set(value) {
            mAdapter.notifyDataSetChanged()
            field = value
        }

    var spanCount = 3
        set(value) {
            layoutManager = GridLayoutManager(context, value)
            field = value
        }

    private val mItems = mutableListOf<Any>()
    private val mAdapter = Adapter()

    private val mItemViewBinders = mutableMapOf<Int, ItemViewBinder<*, *>>()

    init {
        mItems.add(AddItem())
        layoutManager = GridLayoutManager(context, spanCount)
        adapter = mAdapter
    }

    fun register(
        imageItemViewBinder: ImageItemViewBinder<*>,
        addItemViewBinder: AddItemViewBinder<*>
    ) {
        mItemViewBinders[VIEW_TYPE_IMAGE_ITEM] = imageItemViewBinder
        mItemViewBinders[VIEW_TYPE_ADD_ITEM] = addItemViewBinder
    }

    fun setItems(paths: List<String>) {
        val index = mItems.size - 1
        mItems.addAll(index, paths)
        mAdapter.notifyDataSetChanged()

        checkMaxCount()
    }

    fun addItem(path: String) {
        val index = mItems.size - 1
        mItems.add(index, path)
        mAdapter.notifyItemInserted(index)

        checkMaxCount()
    }

    fun addItem(paths: List<String>) {
        val index = mItems.size - 1
        mItems.addAll(index, paths)
        mAdapter.notifyItemRangeInserted(index, paths.size)

        checkMaxCount()
    }

    fun removeItem(path: String) {
        val index = mItems.indexOf(path)
        mItems.removeAt(index)
        mAdapter.notifyItemRemoved(index)

        checkMinCount()
    }

    fun getItems(): List<String> {
        val items = mutableListOf<String>()
        for (item in mItems) {
            if (item is String) {
                items.add(item)
            }
        }
        return items
    }

    private fun checkMaxCount() {
        if (mItems.size > maxCount && mItems.last() is AddItem) {
            val index = mItems.size - 1
            mItems.removeAt(index)
            mAdapter.notifyItemRemoved(index)
        }
    }

    private fun checkMinCount() {
        if (mItems.size < maxCount && mItems.last() !is AddItem) {
            mItems.add(AddItem())
            mAdapter.notifyItemInserted(mItems.size - 1)
        }
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