package demo.simple.addimageview

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import me.simple.view.AddImageView

class InnerImageItemViewBinder :
    AddImageView.ImageItemViewBinder<InnerImageItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.aiv_item_image, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(
        addImageView: AddImageView,
        holder: ViewHolder,
        item: String
    ) {
        holder.ivCover.setImageBitmap(BitmapFactory.decodeFile(item))
        holder.ivDel.setOnClickListener { addImageView.removeItem(item) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView by lazy { itemView.findViewById(R.id.ivCover) }
        val ivDel: ImageView by lazy { itemView.findViewById(R.id.ivDel) }
    }
}
