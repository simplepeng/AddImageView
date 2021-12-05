package me.simple.view

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

open class InnerItemViewDelegate : AddImageView.ItemViewDelegate<InnerItemViewDelegate.VH>() {

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
        val bitmap = BitmapFactory.decodeFile(path)
        holder.ivCover.setImageBitmap(bitmap)

        holder.ivDel.setOnClickListener {
            addImageView.removeItem(holder.bindingAdapterPosition)
        }
    }

     inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        val ivDel: ImageView = itemView.findViewById(R.id.ivDel)
    }
}