package com.legacy07.ytcdl

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     logger(execCmd("wget").toString())

        val sdcard: File = Environment.getDownloadCacheDirectory()
        logger(sdcard.toString())

        logger(packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir);

        setContentView(R.layout.activity_main)

        downloadYtdl(this, packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir).execute()

       // downloadytdl("https://yt-dl.org/downloads/latest/youtube-dl",packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir)
    }

    @Throws(IOException::class)
    fun execCmd(cmd: String?): String? {
        val s = Scanner(Runtime.getRuntime().exec(cmd).inputStream).useDelimiter("\\A")
        return if (s.hasNext()) s.next() else "got nothing"
    }

    fun downloadytdl(link: String, path: String) {

    }
}