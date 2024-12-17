package com.example.u.uitls

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object Installer {
    fun downloadAndInstallApk(context: Context, apkUrl: String, fileName: String) {
        // Step 1: 下载 APK 文件
        downloadApk(context, apkUrl, fileName) { downloadedFile ->
            if (downloadedFile != null && downloadedFile.exists()) {
                // Step 2: 安装 APK
                installApk(context, downloadedFile)
            } else {

            }
        }
    }

    fun downloadApk(
        context: Context,
        apkUrl: String,
        fileName: String,
        onDownloadComplete: (File?) -> Unit
    ) {
        val client = OkHttpClient()
        val request = Request.Builder().url(apkUrl).build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val file = File(context.cacheDir, fileName) // 存储在应用缓存目录
                val inputStream: InputStream = response.body?.byteStream() ?: return

                try {
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(4096)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                    onDownloadComplete(file) // 下载完成后回调
                } catch (e: IOException) {
                    e.printStackTrace()
                    onDownloadComplete(null)
                } finally {
                    inputStream.close()
                }
            } else {
                onDownloadComplete(null)
            }
        }
    }

    fun installApk(context: Context, apkFile: File) {
        val uri: Uri =
            // 使用 FileProvider
            FileProvider.getUriForFile(context, context.packageName + ".fileprovider", apkFile)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        // 需要添加对 Uri 的授权
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        context.startActivity(intent)
    }

    fun getFileSizeWithOkHttp(url: String): Long {
        val client = OkHttpClient()
        var fileSize: Long = -1

        try {
            val request = Request.Builder()
                .url(url)
                .head() // 使用HEAD请求
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                fileSize = response.headers["Content-Length"]?.toLong() ?: -1
            }
            response.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return fileSize
    }

}