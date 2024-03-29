
| 文件名称 | 文件类型 | 访问方法 | 所需权限 | 其他应用是否可以访问？ | 卸载应用时是否移除文件？ |
| --- | --- | --- | --- |----|------|
|  应用专属文件   |  仅供您的应用使用的文件   |  从内部存储空间访问，可以使用 getFilesDir() 或 getCacheDir() 方法<br/>从外部存储空间访问，可以使用 getExternalFilesDir() 或 getExternalCacheDir() 方法   |  从内部存储空间访问不需要任何权限   | 否  | 是    |


### 应用专属文件

Context#getFilesDir() 获取的是应用的私有目录，不需要权限。也不需要在 AndroidManifest.xml 中声明权限。
Context#getExternalFilesDir() 获取的是应用的私有目录，不需要权限。也不需要在 AndroidManifest.xml 中声明权限。


* 在较低版本的 Android 系统中，应用需要声明 READ_EXTERNAL_STORAGE 权限才能访问位于外部存储空间中应用专属目录之外的任何文件。此外，应用需要声明 WRITE_EXTERNAL_STORAGE 权限才能向应用专属目录以外的任何文件写入数据。


### 从 MediaStore 获取图片

关于分区存储，targetSdkVersion 设置为>=30，同时声明了有读写权限。能从 MediaStore 获取图片，也能从系统相册选择图片。但是Glide加载不出来。报错如下：

```
class com.bumptech.glide.load.engine.GlideException: Failed to load resource
There were 3 root causes:
java.io.FileNotFoundException(/storage/emulated/0/Pictures/xx_vc_chat_screenshot.jpg: open failed: EACCES (Permission denied))
java.io.FileNotFoundException(open failed: EACCES (Permission denied))
java.io.FileNotFoundException(open failed: EACCES (Permission denied))
```

解决方法：必须在 AndroidManifest.xml 文件中添加 android:requestLegacyExternalStorage=true属性。让android10及以下机型继续使用旧存储模型。


### 加载其他应用的 ExternalFilesDir

targetSdkVersion >= 30 时，Android10 要有读写权限，并且在 AndroidManifest.xml 中声明 requestLegacyExternalStorage=true


* [数据和文件存储概览](https://developer.android.com/training/data-storage?hl=zh-cn)
* [适配Android 11获取本地相册的图片](https://www.jianshu.com/p/3dffb7ad7971)
  adb shell appops set --uid com.hm.camerademo MANAGE_EXTERNAL_STORAGE allow