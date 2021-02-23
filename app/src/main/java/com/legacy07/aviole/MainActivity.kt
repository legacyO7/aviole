package com.legacy07.aviole

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

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


        setContentView(R.layout.activity_main)
       // logger(execCmd(packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir+"/youtube-dl").toString())
        downloadYtdl(this, appPath).execute()
 }

}