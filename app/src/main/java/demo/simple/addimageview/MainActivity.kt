package demo.simple.addimageview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import me.simple.view.AddImageView
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    private val addImageView by lazy { findViewById<AddImageView>(R.id.addImageView) }
    private val btnAddImage by lazy { findViewById<Button>(R.id.btnAddImage) }
    private val btnGetItems by lazy { findViewById<Button>(R.id.btnGetItems) }
    private val tvItems by lazy { findViewById<TextView>(R.id.tvItems) }

    private val mImages by lazy {
        mutableListOf(
            "https://img2.baidu.com/it/u=111010057,1963484761&fm=26&fmt=auto",
            "https://img0.baidu.com/it/u=680204813,2408273793&fm=26&fmt=auto",
            "https://img0.baidu.com/it/u=4198545283,538361922&fm=26&fmt=auto",
            "https://img2.baidu.com/it/u=1042301913,584297321&fm=26&fmt=auto",
            "https://img0.baidu.com/it/u=3329176626,3830333762&fm=26&fmt=auto",
            "https://img1.baidu.com/it/u=2199515026,1341231739&fm=26&fmt=auto",
            "https://img2.baidu.com/it/u=1094955877,138168367&fm=26&fmt=auto"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addImageView.maxCount = 9

        addImageView.registerItemViewDelegate(CustomItemView())
//        addImageView.registerAddViewDelegate(CustomAddView())

        addImageView.setItems(mImages)

        addImageView.onAddViewClickListener = {
            reqPermission()
        }
        addImageView.onItemViewClickListener = { position, path ->
            toast(position.toString())
        }

        btnAddImage.setOnClickListener {
            reqPermission()
        }

        btnGetItems.setOnClickListener {
            getItems()
        }
    }

    private fun getItems() {
        val items = addImageView.getItems()
        val builder = StringBuilder()
        for (path in items) {
            builder.append(path).append("\n")
        }
        tvItems.text = builder.toString()
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun reqPermission() {
        AndPermission.with(this)
            .runtime()
            .permission(
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE,
                Permission.CAMERA
            )
            .onGranted {
                choiceImages()
            }
            .start()
    }

    private fun choiceImages() {
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(Int.MAX_VALUE)
            .imageEngine(GlideEngine())
            .forResult(110)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 110 && resultCode == RESULT_OK) {
            val paths = Matisse.obtainPathResult(data)

            addImageView.addItem(paths)

//            val path = paths.first()
//            addImageView.addItem(path)

//            addImageView.setItems(paths)
        }
    }
}