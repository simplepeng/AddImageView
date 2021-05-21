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
            adapter?.notifyDataSetChanged()
            field = value
        }

    var spanCount = 3
        set(value) {
            layoutManager = GridLayoutManager(context, value)
            field = value
        }


    init {

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

    internal inner class InnerAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount() = mItems.size

        override fun getItemViewType(position: Int): Int {

        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {

        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {

        }
    }

    abstract class Adapter<T> {
        abstract fun onCreateImageItemViewHolder()
        abstract fun onBindImageItemViewHolder()

        abstract fun onCreateAddItemViewHolder()
        abstract fun onBindAddItemViewHolder()
    }
}