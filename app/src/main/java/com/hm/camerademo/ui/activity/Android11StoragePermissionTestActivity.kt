package com.hm.camerademo.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hm.camerademo.R
import com.hm.camerademo.databinding.ActivityAndroid11StoragePermissionTestBinding
import com.hm.camerademo.util.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Created by p_dmweidu on 2024/3/21
 * Desc: 测试 Android 10，Android 11 存储权限相关功能
 */
class Android11StoragePermissionTestActivity : AppCompatActivity() {

    companion object {


        private const val TAG = "Android11StoragePermiss"

        @JvmStatic
        fun launch(context: Context) {
            val starter = Intent(context, Android11StoragePermissionTestActivity::class.java)
            context.startActivity(starter)
        }

    }

    private lateinit var binding: ActivityAndroid11StoragePermissionTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAndroid11StoragePermissionTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetFiles.setOnClickListener {
            test1()
        }
        binding.btnGetExternalFiles.setOnClickListener {
            test2()
        }
        binding.btnGetOtherAppExternalFiles.setOnClickListener {
            test3()
        }
    }

    /**
     * Context#getFilesDir() 获取的是应用的私有目录，不需要权限。也不需要在 AndroidManifest.xml 中声明权限。
     */
    private fun test1() {
        lifecycleScope.launch(Dispatchers.IO) {
            val filesDir = this@Android11StoragePermissionTestActivity.filesDir
            val txtFile = File(filesDir, "test.txt")
            txtFile.writeText("test")

            this@Android11StoragePermissionTestActivity.filesDir.listFiles()?.forEach {
                Log.i(TAG, "test1: file: ${it.absolutePath}")
            }

        }
    }

    /**
     * Context#getExternalFilesDir() 获取的是应用的私有目录，不需要权限。也不需要在 AndroidManifest.xml 中声明权限。
     */
    private fun test2() {
        lifecycleScope.launch(Dispatchers.IO) {
            val filesDir = getExternalFilesDir(null)
            val txtFile = File(filesDir, "test.txt")
            txtFile.writeText("test")

            getExternalFilesDir(null)?.listFiles()?.forEach {
                Log.i(TAG, "test2: file: ${it.absolutePath}")
            }

            val pictureDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            val pictureFile = File(pictureDir, "test.jpg")
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_big_news)
            // 将Bitmap压缩为JPEG格式写入文件，use 自动关流（异常也不漏）
            FileOutputStream(pictureFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
            getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
                ?.listFiles()?.forEach {
                    Log.i(TAG, "test2: file: ${it.absolutePath}")
                }
        }
    }

    /**
     * targetSdkVersion >= 30 时，Android10 要有读写权限，并且在 AndroidManifest.xml 中声明 requestLegacyExternalStorage=true
     */
    private fun test3() {
        lifecycleScope.launch(Dispatchers.IO) {
            //其他应用的包名 com.hm.viewdemo
            var lastPngFilePath: String? = null
            val otherAppExternalFileDir =
                File("/storage/emulated/0/Android/data/com.hm.viewdemo/files/bubblepng")
            val listFiles = otherAppExternalFileDir.listFiles()
            if (listFiles.isNullOrEmpty()) {
                Log.i(TAG, "test3: otherAppExternalFileDir is ${listFiles.contentToString()}")
                return@launch
            }
            listFiles.forEach {
                Log.i(TAG, "test3: file: ${it.absolutePath}")
                if (it.absolutePath.endsWith(".png")) {
                    lastPngFilePath = it.absolutePath
                }
            }
            withContext(Dispatchers.Main) {
                ImageUtil.load(
                    this@Android11StoragePermissionTestActivity,
                    lastPngFilePath,
                    binding.ivBigImage
                )
            }
        }
    }


}