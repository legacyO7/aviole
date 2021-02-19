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

        setContentView(R.layout.activity_main)
       // logger(execCmd(packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir+"/youtube-dl").toString())
        downloadYtdl(this, packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir).execute()
 }

}