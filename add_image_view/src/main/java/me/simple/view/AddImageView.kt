package me.simple.view

import android.content.Context
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class AddImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    internal companion object {
        const val TAG = "AddImageView"
        const val VIEW_TYPE_IMAGE_ITEM = 1
        const val VIEW_TYPE_ADD_ITEM = 2
    }

    private val mItems = mutableListOf<String>()
    private val mAdapter = InnerAdapter()

    private var mItemViewDelegate: ItemViewDelegate<*>? = InnerItemViewDelegate()
    private var mAddViewDelegate: AddViewDelegate<*>? = InnerAddViewDelegate()

    /** 点击事件 */
    var onItemViewClickListener: ((position: Int, path: String) -> Unit)? = null
    var onAddViewClickListener: ((position: Int) -> Unit)? = null

    /** 拖拽排序 */
    private var mItemTouchHelper: ItemTouchHelper? = null

    private val mVibrator by lazy { context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }

    /** 是否开启震动 */
    private var enableVibrate: Boolean = true

    /** 震动时长 */
    private var vibrateDuration: Long = 100L

    /** 动画的相关配置 */
    var enableAnimation: Boolean = true
    var animDuration: Long = 100
    var scaleValue: Float = 1.1f

    /** 总共的itemCount */
    var maxCount = Int.MAX_VALUE
        set(value) {
            adapter?.notifyDataSetChanged()
            field = value
        }

    /** 一排的itemCount */
    var spanCount = 3
        set(value) {
            layoutManager = GridLayoutManager(context, value)
            field = value
        }

    /** item之间的间距 */
    var itemGap: Int = 0
        set(value) {
            addItemDecoration(ItemGapDecoration(value), 0)
            field = value
        }

    /** 是否开启拖拽排序 */
    var enableDrag: Boolean = true
        set(value) {
            enableDrag(value)
            field = value
        }

    /**
     *
     */
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

        enableDrag = typeArray.getBoolean(R.styleable.AddImageView_enableDrag, true)
        enableVibrate = typeArray.getBoolean(R.styleable.AddImageView_enableVibrate, true)
        vibrateDuration = typeArray.getInt(R.styleable.AddImageView_vibrateDuration, 100).toLong()

        enableAnimation = typeArray.getBoolean(R.styleable.AddImageView_enableAnimation, true)
        animDuration = typeArray.getInt(R.styleable.AddImageView_animDuration, 100).toLong()
        scaleValue = typeArray.getFloat(R.styleable.AddImageView_scaleValue, 1.1f)
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
     * Rv的Adapter
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

    /**
     *
     */
    internal interface InnerViewDelegate<VH : ViewHolder> {
        fun onCreateViewHolder(parent: ViewGroup): VH
        fun onBindViewHolder(
            holder: VH,
            position: Int,
            addImageView: AddImageView
        )
    }

    /**
     * ItemView的代理
     */
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

    /**
     * AddView的代理
     */
    abstract class AddViewDelegate<VH : ViewHolder> : InnerViewDelegate<VH>

    /**
     * 拖拽的开关
     */
    private fun enableDrag(enable: Boolean) {
        if (!enable) {
            mItemTouchHelper?.attachToRecyclerView(null)
        }
        mItemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder
            ): Int {
                if (dragTargetIsAddView(viewHolder)) {
                    return 0
                }

                val dragFlags =
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
                val swipeFlags = 0
                return makeMovementFlags(
                    dragFlags,
                    swipeFlags
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                if (dragTargetIsAddView(target)) {
                    return false
                }

                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                if (fromPosition != toPosition) {
                    vibrate()
                    java.util.Collections.swap(mItems, fromPosition, toPosition)
                    adapter?.notifyItemMoved(fromPosition, toPosition)
                    return true
                }

                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
            }

            override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    vibrate()
                    startScaleAnim(viewHolder)
                }
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                cancelScaleAnim(viewHolder)
            }
        })
        mItemTouchHelper?.attachToRecyclerView(this)
    }

    /**
     * 开始缩放动画
     */
    private fun startScaleAnim(viewHolder: ViewHolder?) {
        if (!enableAnimation) return

        val itemView = viewHolder?.itemView ?: return
        itemView.animate()
            .scaleX(scaleValue)
            .scaleY(scaleValue)
            .setDuration(animDuration)
            .start()
    }

    /**
     * 取消缩放动画
     */
    private fun cancelScaleAnim(viewHolder: ViewHolder?) {
        if (!enableAnimation) return

        val itemView = viewHolder?.itemView ?: return
        itemView.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .setDuration(animDuration)
            .start()
    }

    /**
     * 拖拽的是否是AddView
     */
    private fun dragTargetIsAddView(
        target: ViewHolder
    ): Boolean {
        val adapter = adapter ?: return false

        if (mItems.size < maxCount && target.adapterPosition == adapter.itemCount - 1) {
            return true
        }

        return false
    }

    /**
     * 震动的方法
     */
    private fun vibrate() {
        if (!enableVibrate) return

        mVibrator.vibrate(vibrateDuration)
    }
}