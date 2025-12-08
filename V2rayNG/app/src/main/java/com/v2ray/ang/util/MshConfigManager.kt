package com.v2ray.ang.util

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.IOException

object MshConfigManager {

    private const val TAG = "ConfigManager"
    private const val FOLDER_PATH ="/sdcard/msh"
    private const val FILE_NAME = "V2rayNG.json"
    private const val KEY_VERSION = "version"

    /**
     * 执行检查和更新逻辑
     */
    fun checkAndUpdateVersion(context: Context) {
        // 0. 获取当前 App 版本号
        val currentVersion = getAppVersionName(context) ?: return
        Log.d(TAG, "当前 App 版本: $currentVersion")

        val dir = File(FOLDER_PATH)
        val file = File(dir, FILE_NAME)

        try {
            // 确保目录存在
            if (!dir.exists()) {
                dir.mkdirs()
            }

            // 1. 判断文件是否存在
            if (!file.exists()) {
                Log.d(TAG, "文件不存在，正在创建并写入...")
                writeVersionToFile(file, currentVersion)
            } else {
                // 3. 如果存在，读取并判断
                val content = file.readText()
                if (content.isBlank()) {
                    // 文件为空的情况
                    writeVersionToFile(file, currentVersion)
                    return
                }

                try {
                    val jsonObject = JSONObject(content)
                    val savedVersion = jsonObject.optString(KEY_VERSION)

                    Log.d(TAG, "文件中记录的版本: $savedVersion")

                    if (savedVersion != currentVersion) {
                        Log.d(TAG, "版本不一致，正在更新文件...")
                        writeVersionToFile(file, currentVersion)
                    } else {
                        Log.d(TAG, "版本一致，无需更新。")
                    }
                } catch (e: Exception) {
                    // JSON 解析失败（格式错误），覆盖写入
                    Log.e(TAG, "JSON 解析失败，重置文件", e)
                    writeVersionToFile(file, currentVersion)
                }
            }

        } catch (e: IOException) {
            Log.e(TAG, "文件读写发生错误", e)
        }
    }

    /**
     * 将版本号写入 JSON 文件
     */
    private fun writeVersionToFile(file: File, version: String) {
        try {
            val jsonObject = JSONObject()
            jsonObject.put(KEY_VERSION, version)
            
            // 写入文件
            file.writeText(jsonObject.toString())
            Log.d(TAG, "写入成功: ${jsonObject.toString()}")
        } catch (e: Exception) {
            Log.e(TAG, "写入失败", e)
        }
    }

    /**
     * 获取 App 版本名
     */
    private fun getAppVersionName(context: Context): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}