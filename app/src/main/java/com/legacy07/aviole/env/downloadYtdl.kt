package com.legacy07.aviole.env

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class downloadYtdl(private val context: Context, path: String, progressDialog: ProgressDialog) :
    AsyncTask<String, Int, String>() {
    val filePath = "$path/youtube-dl";
    val folderPath = path;
    val tarPath = "$path/avioleTPeM.tar.gz";
    val ytdl: File = File(filePath)
    var mWakeLock: WakeLock? = null
    var mProgressDialog = progressDialog

    override fun doInBackground(vararg params: String?): String {
        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(params[0])
            HttpsTrustManager.allowAllSSL()
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            // this will be useful so that you can show a typical 0-100% progress bar
            val fileLength = connection.contentLength

            // download the file
            input = BufferedInputStream(url.openStream())
            output = FileOutputStream(filePath)
            val data = ByteArray(1024)
            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                total += count.toLong()
                // publishing the progress....
                publishProgress((total * 100 / fileLength).toInt())
                output.write(data, 0, count)
            }
            output.flush()
            output.close()
            input.close()
        } catch (e: Exception) {
            return e.toString();
        } finally {
            try {
                output?.close();
                input?.close();
            } catch (ignore: IOException) {
            }

            connection?.disconnect();
        }
        return "null";
    }

    override fun onPreExecute() {
        super.onPreExecute()
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            javaClass.name
        )
        mWakeLock?.acquire(10 * 60 * 1000L /*10 minutes*/)
        mProgressDialog.show()
        logger("started")
    }

    override fun onProgressUpdate(vararg progress: Int?) {
        super.onProgressUpdate(*progress)
        mProgressDialog.isIndeterminate = false
        mProgressDialog.max = 100
        mProgressDialog.progress = progress[0]!!
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        mWakeLock?.release();
        mProgressDialog.dismiss();
        if (result != "null")
            Toast.makeText(context, "Download error: $result", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

        ytdl.setExecutable(true)


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

//            executeAction(
//                folderPath,
//                "$folderPath/files/usr/bin/python",
//                arrayOf("youtube-dl", "-v")
//            )
        }

    }
}