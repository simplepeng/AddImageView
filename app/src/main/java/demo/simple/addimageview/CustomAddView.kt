package demo.simple.addimageview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.simple.view.AddImageView

class CustomAddView : AddImageView.AddViewDelegate<CustomAddView.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.custom_add_view, parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int, addImageView: AddImageView) {

    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
}