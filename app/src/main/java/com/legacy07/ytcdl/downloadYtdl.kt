package com.legacy07.ytcdl

import android.content.Context
import android.os.AsyncTask
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class downloadYtdl(context: Context, path: String) : AsyncTask<Void, Void, Boolean>() {
    val filePath=path;
    val context=context;
    override fun doInBackground(vararg params: Void?): Boolean {
        if(File("$filePath/youtube-dl").exists())
            logger("file exists")
        else {
            HttpsTrustManager.allowAllSSL()
            URL("https://yt-dl.org/downloads/latest/youtube-dl")
                .openStream().use { input ->
                    logger(input.toString())
                    FileOutputStream("$filePath/youtube-dl").use { output ->
                        input.copyTo(output)
                    }
                }
        }
        return true;
    }

    override fun onPreExecute() {
        super.onPreExecute()

        logger("started")
        // ...
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)

        logger("Done !!!")

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(context))
            Python.getInstance().getModule("$filePath/youtube-dl")
            logger("here we went")
        }



        // ...
    }
}