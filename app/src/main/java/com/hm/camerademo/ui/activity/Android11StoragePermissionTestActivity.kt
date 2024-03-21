package com.hm.camerademo.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hm.camerademo.R
import com.hm.camerademo.databinding.ActivityAndroid11StoragePermissionTestBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

    }

    /**
     * Context#getFilesDir() 获取的是应用的私有目录，不需要权限。也不需要在 AndroidManifest.xml 中声明权限。
     */
    private fun test1() {
        GlobalScope.launch(Dispatchers.Main) {
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
        GlobalScope.launch(Dispatchers.Main) {
            val filesDir = this@Android11StoragePermissionTestActivity.getExternalFilesDir(null)
            val txtFile = File(filesDir, "test.txt")
            txtFile.writeText("test")

            this@Android11StoragePermissionTestActivity.getExternalFilesDir(null)?.listFiles()
                ?.forEach {
                    Log.i(TAG, "test2: file: ${it.absolutePath}")
                }

            val pictureDir =
                this@Android11StoragePermissionTestActivity.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            val pictureFile = File(pictureDir, "test.jpg")
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_big_news)
            // 创建一个新的文件输出流
            val fos = FileOutputStream(pictureFile)
            // 将Bitmap压缩为JPEG格式，然后写入文件输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            // 关闭文件输出流
            fos.close()

            this@Android11StoragePermissionTestActivity.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
                ?.listFiles()?.forEach {
                Log.i(TAG, "test2: file: ${it.absolutePath}")
            }
        }
    }


}