package demo.simple.addimageview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.simple.view.AddImageView
import me.simple.view.AddItem

class InnerAddItemViewBinder(
    private val onItemClick: () -> Unit
) : AddImageView.AddItemViewBinder<InnerAddItemViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.aiv_item_add, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(
        addImageView: AddImageView,
        holder: ViewHolder,
        item: AddItem
    ) {
        holder.itemView.setOnClickListener { onItemClick() }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
