package com.legacy07.aviole.misc

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import com.legacy07.aviole.env.downloadYtdl
import com.legacy07.aviole.env.extractTar
import com.legacy07.aviole.env.logger
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection

class getFilesize: AsyncTask<String, String, String>() {

    var fileSize:String?=""

    override fun doInBackground(vararg params: String): String? {
        try {
            val url:URL =  URL(params[0])
            logger(params[0])
            val urlConnection: URLConnection = url.openConnection()
            urlConnection.connect()
            logger(urlConnection.contentLength.toString())
           fileSize = (urlConnection.contentLength/(1024*1024)).toString()
        } catch (e: IOException) {
           logger(e.toString())
        }
        return fileSize
    }
}

fun initYTDLdownload(context: Context,mProgressDialog:ProgressDialog){
    mProgressDialog.setMessage("Downloading youtube-dl binary [ ${getFilesize().execute(ytdl_url).get()}M ]")
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    mProgressDialog.setCancelable(true);

    val downloadFile = downloadYtdl(context, "$appPath/youtube-dl", mProgressDialog)
    downloadFile.execute(ytdl_url)

    mProgressDialog.setOnCancelListener {
        downloadFile.cancel(true) //cancel the task
    }
}

fun extract_aviole_tarball(mProgressDialog:ProgressDialog){
    mProgressDialog.setMessage("Extracting aviole module")
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    extractTar(File(tarPath), File(appPath))
    mProgressDialog.cancel()
}

fun initAvioleModuleDownload(context: Context,mProgressDialog:ProgressDialog){
    val alertDialogBuilder = AlertDialog.Builder(context)
    alertDialogBuilder.setTitle("Download avioleModule [ ${getFilesize().execute(
        aviole_module_URL).get()}M ]")
        ?.setMessage("Trust me! You wont regret it!")
        ?.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
            mProgressDialog.setMessage("Downloading aviole module")
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            val downloadFile = downloadYtdl(context, "$appPath/avioleTPeM",
                mProgressDialog
            )
            downloadFile.execute(aviole_module_URL)

            mProgressDialog.setOnCancelListener {
                downloadFile.cancel(true) //cancel the task
            }

        }
        ?.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        ?.show()

}


class misc {
}