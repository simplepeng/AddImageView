package demo.simple.addimageview

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        mapOf(
            "https://img0.baidu.com/it/u=962361882,2281204904&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500" to 0,
            "https://img1.baidu.com/it/u=1507025561,3635944319&fm=253&fmt=auto&app=138&f=JPEG?w=400&h=711" to 1,
            "https://img0.baidu.com/it/u=3274713675,3050580903&fm=253&fmt=auto&app=138&f=JPEG?w=800&h=500" to 2,
            "https://img0.baidu.com/it/u=3098099923,3369715583&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500" to 3,
            "https://img0.baidu.com/it/u=2651423003,3415459398&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500" to 4,
            "https://img0.baidu.com/it/u=501412934,626408000&fm=253&fmt=auto&app=120&f=JPEG?w=1024&h=640" to 5,
            "https://img1.baidu.com/it/u=577114546,2771402770&fm=253&fmt=auto&app=120&f=JPEG?w=1280&h=800" to 6
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addImageView.maxCount = 9

        addImageView.registerItemViewDelegate(CustomItemView())
//        addImageView.registerAddViewDelegate(CustomAddView())

        addImageView.setItems(mImages.keys.toList())

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
        builder.append("\n")
        for (path in items) {
            builder.append(path)
                .append(" -- ").append(mImages[path].toString())
                .append("\n")
        }
        val itemsStr = builder.toString()
        tvItems.text = itemsStr
        Log.d("getItems --- ", itemsStr)
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