package com.legacy07.aviole.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.legacy07.aviole.R
import com.legacy07.aviole.misc.*

class avInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_av_info)
        val ytdltv=findViewById<TextView>(R.id.ytdl_text)
        executeToTextView(this,ytdltv, avCommand("--version")
        )

        val downloadytdlbutton=findViewById<ImageView>(R.id.downloadytdl)
        downloadytdlbutton.setOnClickListener{
            initYTDLdownload(this,true)
        }
    }
}