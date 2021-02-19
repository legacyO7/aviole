package com.legacy07.ytcdl

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class downloadYtdl(context: Context, path: String) : AsyncTask<Void, Void, Boolean>() {
    val filePath= "$path/youtube-dl";
    val folderPath= "$path";
    val tarPath= "$path/miraklebox";
    private val context=context;
    val ytdl:File=File(filePath)
    override fun doInBackground(vararg params: Void?): Boolean {
        ytdl.setExecutable(true)
        ytdl.canExecute()

        if(ytdl.exists())
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
        if(   ytdl.canExecute())
            logger("it is exectable")
        else
            logger("not exec")
        if(!File("$folderPath/files").exists())
        extractTar(File(tarPath),File(folderPath))

       val file:File= File("$folderPath/files/usr/bin/")
        file.setWritable(true)
        file.setReadable(true)
        file.setExecutable(true)
     //   logger(execCmd("su")!!)
       // changePermission(file)
        changePermission(File("$folderPath/files/usr/lib/libpython3.9.so.1.0"))
        logger(execCmd("uname")!!)
   logger("./python".runCommand(file)!!)
    }
}