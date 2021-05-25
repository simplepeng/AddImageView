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
            "https://gank.io/images/ce66aa74d78f49919085b2b2808ecc50",
            "https://gank.io/images/02eb8ca3297f4931ab64b7ebd7b5b89c",
            "https://gank.io/images/0f536c69ada247429b8a9e38d3dba8bb",
            "https://gank.io/images/ccf0316264d245018fc651cffa6e90de",
            "https://gank.io/images/95ddb01b6bd34a85aedfda4c9e9bd003",
            "https://gank.io/images/e92911f5ff9446d5a899b652b1934b93",
//            "https://gank.io/images/6e57b254da79416bbe58248b570ea85f",
//            "https://gank.io/images/e0b652d2a0cb46ba888a935c525bd312",
//            "https://gank.io/images/95ddb01b6bd34a85aedfda4c9e9bd003",
//            "https://gank.io/images/4817628a6762410895f814079a6690a1",
//            "https://gank.io/images/0f536c69ada247429b8a9e38d3dba8bb",
//            "https://gank.io/images/1a515f1508e345e2bf24673c2c2d50c4",
//            "https://gank.io/images/4002b1fd18544802b80193fad27eaa62"
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