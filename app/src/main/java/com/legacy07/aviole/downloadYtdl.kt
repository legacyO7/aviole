package com.legacy07.aviole

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class downloadYtdl(context: Context, path: String) : AsyncTask<Void, Void, Boolean>() {
    val filePath = "$path/youtube-dl";
    val folderPath = "$path";
    val tarPath = "$path/avioleTPeM.tar.gz";
    private val context = context;
    val ytdl: File = File(filePath)
    override fun doInBackground(vararg params: Void?): Boolean {
        ytdl.setExecutable(true)
        ytdl.canExecute()

        if (ytdl.exists())
            logger("file exists")
        else {
            HttpsTrustManager.allowAllSSL()
            URL("https://yt-dl.org/downloads/latest/youtube-dl")
                .openStream().use { input ->
                    logger(input.toString())
                    FileOutputStream(filePath).use { output ->
                        input.copyTo(output)
                    }
                }
            ytdl.setExecutable(true)
        }
        return true;
    }

    override fun onPreExecute() {
        super.onPreExecute()

        logger("started")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)

        logger("Done !!!")
        if (!File("$folderPath/files").exists()) {
            if (!File(tarPath).exists())
                logger("aviole module not found")
            else {
                logger("Extracting files")
                extractTar(File(tarPath), File(folderPath))
                logger("Extracted !!")
            }
        } else {
            //   changePermission(File("$folderPath/files/usr/bin/youtube-dl"))

            executeAction(
                folderPath,
                "$folderPath/files/usr/bin/python",
                arrayOf("youtube-dl", "-v")
            )
        }

    }
}