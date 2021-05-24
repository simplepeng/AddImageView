package me.simple.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
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

    private var mItemViewDelegate: ItemViewDelegate<*>? = InnerItemViewDelegate()
    private var mAddViewDelegate: AddViewDelegate<*>? = InnerAddViewDelegate()

    var onItemViewClickListener: ((position: Int, path: String) -> Unit)? = null
    var onAddViewClickListener: ((position: Int) -> Unit)? = null

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

    /**item之间的间距*/
    var itemGap: Int = 0
        set(value) {
            addItemDecoration(ItemGapDecoration(value), 0)
            field = value
        }

    open fun dp2px(value: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            resources.displayMetrics
        )
    }

    init {
        this.layoutManager = GridLayoutManager(context, spanCount)
        this.adapter = mAdapter

        initAttrs(attrs)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.AddImageView)
        maxCount = typeArray.getInt(R.styleable.AddImageView_maxCount, Int.MAX_VALUE)
        spanCount = typeArray.getInt(R.styleable.AddImageView_spanCount, 3)
        itemGap = typeArray.getDimension(R.styleable.AddImageView_itemGap, dp2px(1f)).toInt()
        typeArray.recycle()
    }

    /**
     * 设置数据
     */
    fun setItems(paths: List<String>) {
        mItems.clear()
        if (paths.size > maxCount) {
            val subList = paths.subList(0, maxCount)
            mItems.addAll(subList)
        } else {
            mItems.addAll(paths)
        }
        adapter?.notifyDataSetChanged()
    }

    /**
     * 添加一个item
     */
    fun addItem(path: String) {
        mItems.add(path)
        if (mItems.size == maxCount) {
            adapter?.notifyItemChanged(mItems.lastIndex)
        } else {
            adapter?.notifyItemInserted(mItems.lastIndex)
        }
    }

    /**
     * 添加一序列的item
     */
    fun addItem(paths: List<String>) {
        if (mItems.size + paths.size > maxCount) {
            val remainCount = maxCount - mItems.size
            val subList = paths.subList(0, remainCount)
            mItems.addAll(subList)
        } else {
            mItems.addAll(paths)
        }
        adapter?.notifyDataSetChanged()
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
     * 注册ItemView的代理
     */
    fun registerItemViewDelegate(delegate: ItemViewDelegate<*>) {
        this.mItemViewDelegate = delegate
        adapter?.notifyDataSetChanged()
    }

    /**
     * 注册AddView的代理
     */
    fun registerAddViewDelegate(delegate: AddViewDelegate<*>) {
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

        @Suppress("UNCHECKED_CAST")
        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            val viewType = getItemViewType(position)
            val viewDelegate = if (viewType == VIEW_TYPE_ADD_ITEM)
                mAddViewDelegate!!
            else
                mItemViewDelegate!!

            (viewDelegate as InnerViewDelegate<ViewHolder>).onBindViewHolder(
                holder,
                position,
                this@AddImageView
            )

            holder.itemView.setOnClickListener {
                val adapterPosition = holder.adapterPosition
                if (viewType == VIEW_TYPE_ADD_ITEM) {
                    onAddViewClickListener?.invoke(adapterPosition)
                } else {
                    onItemViewClickListener?.invoke(adapterPosition, mItems[adapterPosition])
                }
            }
        }
    }

    interface InnerViewDelegate<VH : ViewHolder> {
        fun onCreateViewHolder(parent: ViewGroup): VH
        fun onBindViewHolder(
            holder: VH,
            position: Int,
            addImageView: AddImageView
        )
    }

    abstract class ItemViewDelegate<VH : ViewHolder> : InnerViewDelegate<VH> {
        override fun onBindViewHolder(
            holder: VH,
            position: Int,
            addImageView: AddImageView
        ) {
            val path = addImageView.getItems()[holder.adapterPosition]
            onBindViewHolder(holder, path, addImageView)
        }

        abstract fun onBindViewHolder(
            holder: VH,
            path: String,
            addImageView: AddImageView
        )
    }

    abstract class AddViewDelegate<VH : ViewHolder> : InnerViewDelegate<VH>
}