package com.legacy07.aviole.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.legacy07.aviole.R
import com.legacy07.aviole.env.*
import com.legacy07.aviole.misc.appPath
import com.legacy07.aviole.misc.executeToTextView
import java.lang.StringBuilder

class avInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_av_info)

        val ytdltv=findViewById<TextView>(R.id.ytdl_text)
        executeToTextView(this,ytdltv,  appPath,
            "$appPath/files/usr/bin/python",
            arrayOf("youtube-dl", "--version"))
    }
}