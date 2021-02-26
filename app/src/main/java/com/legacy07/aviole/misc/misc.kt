package com.legacy07.aviole.misc

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.TextView
import com.legacy07.aviole.env.*
import com.legacy07.aviole.ui.avHome
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLConnection
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import java.util.HashSet

class getFilesize : AsyncTask<String, String, String>() {

    var fileSize: String? = ""

    override fun doInBackground(vararg params: String): String? {
        try {
            val url: URL = URL(params[0])
            logger(params[0])
            val urlConnection: URLConnection = url.openConnection()
            urlConnection.connect()
            logger(urlConnection.contentLength.toString())
            fileSize = (urlConnection.contentLength / (1024 * 1024)).toString()
        } catch (e: IOException) {
            logger(e.toString())
        }
        return fileSize
    }
}

fun initYTDLdownload(context: Context, redownload: Boolean) {

    val mProgressDialog: ProgressDialog = ProgressDialog(context)

    mProgressDialog.setMessage(
        "Downloading youtube-dl binary [ ${
            getFilesize().execute(ytdl_url).get()
        }M ]"
    )
    mProgressDialog.isIndeterminate = true
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
    mProgressDialog.setCancelable(true)
    mProgressDialog.show()

    val downloadFile = downloadYtdl(context, "$appPath/youtube-dl", mProgressDialog, redownload)
    downloadFile.execute(ytdl_url)

    mProgressDialog.setOnCancelListener {
        downloadFile.cancel(true) //cancel the task
    }
}

class extract_aviole_tarball(val context: Context,val redownload: Boolean) : AsyncTask<String, String, Boolean>() {
    val mProgressDialog: ProgressDialog = ProgressDialog(context)
    override fun onPreExecute() {
        super.onPreExecute()
        mProgressDialog.setMessage("Extracting aviole module")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setCancelable(false)
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog.show()
        mProgressDialog.create()
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        if (result) {
            mProgressDialog.dismiss()
            if (File(prefixPath).exists() && File(ytdlPath).exists()&&!redownload) {
                val intent: Intent = Intent(context, avHome::class.java)
                context.startActivity(intent)
            }
        }
    }

    override fun doInBackground(vararg params: String?): Boolean {
        extractTar(File(tarPath), File(appPath))
        return true
    }
}


fun initAvioleModuleDownload(context: Context, redownload: Boolean) {
    val mProgressDialog: ProgressDialog = ProgressDialog(context)
    val alertDialogBuilder = AlertDialog.Builder(context)
    alertDialogBuilder.setTitle(
        "Download avioleModule [ ${
            getFilesize().execute(
                aviole_module_URL
            ).get()
        }M ]"
    )
        ?.setMessage("Trust me! You wont regret it!")
        ?.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
            mProgressDialog.setMessage("Downloading aviole module")
            mProgressDialog.isIndeterminate = true
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            mProgressDialog.setCancelable(true)
            mProgressDialog.show()

            val downloadFile = downloadYtdl(
                context, "$appPath/avioleTPeM",
                mProgressDialog, redownload
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


fun rmFile(fileOrDirectory: File): Boolean {
    if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) rmFile(
        child
    )
    fileOrDirectory.delete()

    return !fileOrDirectory.exists()
}

fun executeToTextView(activity: Activity,tv: TextView, args: Array<String>) {
    val worker = WorkerThread(
        activity, "$prefixPath/bin",
       // "$prefixPath/bin/pip",
        "$binPath/$filetoexec",
        args
     //   arrayOf("-v","--ffmpeg-location","/data/data/com.legacy07.aviole/files/usr/bin/")
    )
    tv.setText("Loading...")

    worker.registerWorkerListener(WorkerListener { thread ->
        println("Work done")
        val results = thread.getResults()
        var sb: StringBuilder = StringBuilder()
        val iter: Iterator<*> = results.iterator()
        while (iter.hasNext()) {
            val result = iter.next() as String
            sb.append(result)
        }
        activity.runOnUiThread(Runnable {
            tv.setText(sb.toString().trim())
        })
    })

    val thread = Thread(worker)
    thread.start()
}

fun avCommand(command: String): Array<String> {
    filetoexec=command.split("|")[0]
    return command.split("|")[1].split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }
        .toTypedArray()
}

fun changePermission(path: File){

    val perms: MutableSet<PosixFilePermission> = HashSet()
    perms.add(PosixFilePermission.OWNER_READ)
    perms.add(PosixFilePermission.OWNER_WRITE)
    perms.add(PosixFilePermission.OWNER_EXECUTE)

    perms.add(PosixFilePermission.OTHERS_READ)
    perms.add(PosixFilePermission.OTHERS_WRITE)
    perms.add(PosixFilePermission.OTHERS_EXECUTE)

    perms.add(PosixFilePermission.GROUP_READ)
    perms.add(PosixFilePermission.GROUP_WRITE)
    perms.add(PosixFilePermission.GROUP_EXECUTE)

    Files.setPosixFilePermissions(path.toPath(), perms)
}

class misc