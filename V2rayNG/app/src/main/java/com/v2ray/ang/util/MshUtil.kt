package com.v2ray.ang.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import cn.hutool.core.map.MapUtil
import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import java.io.File

object MshUtil {
    /**
     * 尝试读取 /sdcard/msh_android_id 文件的内容。
     * 此方法在 Android 11 (API 30) 及更高版本上需要 MANAGE_EXTERNAL_STORAGE 权限。
     * 在 Android 10 (API 29) 及更早版本上，通常需要 READ_EXTERNAL_STORAGE 权限。
     */
    public fun readMshAndroidIdFile(context: Context) : String {
        // 在 Android 11 (API 30) 及更高版本上，首先检查 MANAGE_EXTERNAL_STORAGE 权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Toast.makeText(context, "需要 MANAGE_EXTERNAL_STORAGE 权限才能读取文件", Toast.LENGTH_LONG).show()
            return ""
        }

        // 定义目标文件
        val targetFile = File(Environment.getExternalStorageDirectory(), "msh_android_id")
        if (!targetFile.exists()) {
            Toast.makeText(context, "设备未设置美数合android_id", Toast.LENGTH_LONG).show()
            return ""
        }
        return targetFile.readText()
    }

    /**
     * 尝试读取 /sdcard/msh_android_id 文件的内容。
     * 此方法在 Android 11 (API 30) 及更高版本上需要 MANAGE_EXTERNAL_STORAGE 权限。
     * 在 Android 10 (API 29) 及更早版本上，通常需要 READ_EXTERNAL_STORAGE 权限。
     */
    public fun getInfo(msh_android_id:String) : JSONObject {
        var res = HttpRequest.post("https://automatic-android-api.jobeyond.cn/a/automation/mobile/info").
        timeout(3000).
        header("x-token","2W93grF60JDqnFEI").
        body(JSON.toJSONString(MapUtil.of("id",msh_android_id))).
        execute().body()
        var json = JSONObject.parseObject(res)
        return json.getJSONObject("body")!!
    }
}