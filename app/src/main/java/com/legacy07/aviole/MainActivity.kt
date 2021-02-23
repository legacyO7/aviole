package com.legacy07.aviole

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.File


@JvmField var appPath:String=""
@JvmField var tarPath:String=""
@JvmField var ytdlPath:String=""
@JvmField var homePath:String=""
@JvmField var prefixPath:String=""

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appPath= packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir;
        tarPath="$appPath/miraklebox.tar.gz";
        ytdlPath="$appPath/youtube-dl";
        homePath="$appPath/files/home";
        prefixPath="$appPath/files/usr";

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)
       // logger(execCmd(packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir+"/youtube-dl").toString())
      //  downloadYtdl(this, appPath).execute()

        if(!File(ytdlPath).exists()){
            val mProgressDialog: ProgressDialog = ProgressDialog(this)
            mProgressDialog.setMessage("Downloading youtube-dl binary")
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);

            val downloadFile = downloadYtdl(this, appPath,mProgressDialog)
            downloadFile.execute("https://yt-dl.org/downloads/latest/youtube-dl")

            mProgressDialog.setOnCancelListener {
                downloadFile.cancel(true) //cancel the task
            }
        }

    }

}