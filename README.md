## AddImageView

选择照片的自定义九宫格View，扩展性极高。

* 支持自定义视图
* 支持九宫格显示（设置maxCount）
* 支持拖拽排序（有震动和动画，可以关闭）
* 支持每行item个数设置
* 等等

| 静态图 | 动态图 |
| ------ | ------ |
|   ![](files/add_image_view.jpg)     | ![](files/add_image_view.gif) |



## 导入依赖

[![](https://jitpack.io/v/simplepeng/AddImageView.svg)](https://jitpack.io/#simplepeng/AddImageView)

```groovy
maven { url 'https://jitpack.io' }
```

```groovy
implementation 'com.github.simplepeng:AddImageView:v1.0.1'
```

## 如何使用

在布局中添加`me.simple.view.AddImageView`

```xml
<me.simple.view.AddImageView
        android:id="@+id/addImageView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
```

在代码中注册`ItemViewDelegate`或`AddViewDelegate`，默认也有实现类。

```kotlin
/**
 * 注册ItemView的代理
 */
addImageView.registerItemViewDelegate(CustomItemView())
/**
 * 注册AddView的代理
 */
addImageView.registerAddViewDelegate(CustomAddView())
```

```kotlin
class CustomItemView : AddImageView.ItemViewDelegate<CustomItemView.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_item_view, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, path: String, addImageView: AddImageView) {
		//...
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
    }
}
```

### 可用的属性和方法

```kotlin
/** 是否开启震动 */
var enableVibrate: Boolean = true

/** 震动时长 */
var vibrateDuration: Long = 100L

/** 动画的相关配置 */
var enableAnimation: Boolean = true
var animDuration: Long = 100
var scaleValue: Float = 1.1f

/** 总共的itemCount */
var maxCount = Int.MAX_VALUE

/** 一排的itemCount */
var spanCount = 3

/** item之间的间距 */
var itemGap: Int = 0

/** 是否开启拖拽排序 */
var enableDrag: Boolean = true

/** 点击事件 */
var onItemViewClickListener: ((position: Int, path: String) -> Unit)? = null
var onAddViewClickListener: ((position: Int) -> Unit)? = null

/** 更新数据源的一系列方法 */
fun setItems(paths: List<String>)
fun addItem(path: String)
fun addItem(paths: List<String>)
fun removeItem(path: String)
fun removeItem(position: Int)
fun getItems()
```

## 版本迭代

* v1.0.1：修改属性名，防止冲突
* v1.0.0：首次发布