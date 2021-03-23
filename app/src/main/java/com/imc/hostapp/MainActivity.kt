package com.imc.hostapp

import android.R.mipmap
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import dalvik.system.PathClassLoader
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    var datas = arrayListOf<HashMap<String, String>>()

    private lateinit var image: AppCompatImageView
    private lateinit var btn: AppCompatButton
    private lateinit var btn1: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

    }

    private fun initView() {
        image = findViewById(R.id.image)
        btn = findViewById(R.id.btn)
        btn1 = findViewById(R.id.btn1)
        btn.setOnClickListener {
            val list = findAllPlugin()
            if (list.isNotEmpty()) {
                for (bean in list) {
                    val map: HashMap<String, String> = HashMap()
                    map["label"] = bean.label
                    map["name"] = bean.name
                    datas.add(map)
                }
                Toast.makeText(this, "插件已安装", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "请先安装插件", Toast.LENGTH_SHORT).show()
            }
        }

        btn1.setOnClickListener {

            val pluginContext = createPackageContext(datas[0]["name"], Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE)
            val resourceId = datas[0]["name"]?.let { it1 -> dynamicLoadApk(it1, pluginContext) }
            resourceId?.let { it1 ->
                image.setBackgroundDrawable(pluginContext.resources.getDrawable(it1))
            }

        }
    }


    private fun findAllPlugin(): List<PluginBean> {
        val plugins: MutableList<PluginBean> = ArrayList()
        val pm = packageManager
        //通过包管理器查找所有已安装的apk文件
        val packageInfo = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
        for (info in packageInfo) {
            //得到当前apk的包名
            val pkgName = info.packageName
            //得到当前apk的sharedUserId
            val shareUserId = info.sharedUserId
            //判断这个apk是否是我们应用程序的插件
            if (shareUserId != null && shareUserId == "com.test.app" && pkgName != this.packageName) {
                val label = pm.getApplicationLabel(info.applicationInfo).toString() //得到插件apk的名称
                val bean = PluginBean(label, pkgName)
                plugins.add(bean)
            }
        }
        return plugins
    }

    @Throws(Exception::class)
    private fun dynamicLoadApk(packageName: String, pluginContext: Context): Int {
        //第一个参数为包含dex的apk或者jar的路径，第二个参数为父加载器
        val pathClassLoader = PathClassLoader(pluginContext.packageResourcePath, ClassLoader.getSystemClassLoader())
        //        Class<?> clazz = pathClassLoader.loadClass(packageName + ".R$mipmap");//通过使用自身的加载器反射出mipmap类进而使用该类的功能
        //参数：1、类的全名，2、是否初始化类，3、加载时使用的类加载器
        val clazz = Class.forName("$packageName.R\$mipmap", true, pathClassLoader)
        //使用上述两种方式都可以，这里我们得到R类中的内部类mipmap，通过它得到对应的图片id，进而给我们使用
        val field: Field = clazz.getDeclaredField("logo")
        return field.getInt(mipmap::class.java)
    }

}