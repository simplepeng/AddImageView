package me.simple.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

open class InnerAddViewDelegate : AddImageView.AddViewDelegate<InnerAddViewDelegate.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.aiv_item_add, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        addImageView: AddImageView
    ) {

    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
}
