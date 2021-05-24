package me.simple.view

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class InnerItemViewDelegate : AddImageView.ItemViewDelegate<InnerItemViewDelegate.VH>() {
    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.aiv_item_image, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(
        holder: VH,
        path: String,
        addImageView: AddImageView
    ) {
        val vh = holder as VH

        val bitmap = BitmapFactory.decodeFile(path)
        vh.ivCover.setImageBitmap(bitmap)

        vh.ivDel.setOnClickListener {
            addImageView.removeItem(holder.adapterPosition)
        }
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover = itemView.findViewById<ImageView>(R.id.ivCover)
        val ivDel = itemView.findViewById<ImageView>(R.id.ivDel)
    }
}