package demo.simple.addimageview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import me.simple.view.AddImageView


class MainActivity : AppCompatActivity() {

    private val addImageView by lazy { findViewById<AddImageView>(R.id.addImageView) }
    private val btnAddImage by lazy { findViewById<Button>(R.id.btnAddImage) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addImageView.maxCount = 9

        btnAddImage.setOnClickListener {
            reqPermission()
        }

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
            .maxSelectable(10)
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
        }
    }
}