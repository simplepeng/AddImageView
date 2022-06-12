package demo.simple.addimageview

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import me.simple.view.AddImageView

class CustomItemView : AddImageView.ItemViewDelegate<CustomItemView.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_item_view, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, path: String, addImageView: AddImageView) {
        val multi = MultiTransformation<Bitmap>(
            CenterCrop(),
            RoundedCornersTransformation(10, 0),
        )
        Glide.with(holder.itemView)
            .load(path)
            .apply(RequestOptions.bitmapTransform(multi))
            .into(holder.ivCover)
        holder.ivDel.setOnClickListener {
            addImageView.removeItem(path)
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val ivDel: ImageView = itemView.findViewById(R.id.ivDel)
    }
}