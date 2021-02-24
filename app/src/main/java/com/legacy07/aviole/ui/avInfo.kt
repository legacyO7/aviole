package com.legacy07.aviole.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.legacy07.aviole.R
import com.legacy07.aviole.env.*
import com.legacy07.aviole.misc.appPath
import java.lang.StringBuilder

class avInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_av_info)

        val ytdltv=findViewById<TextView>(R.id.ytdl_text)

        val worker = WorkerThread(  appPath,
            "$appPath/files/usr/bin/python",
            arrayOf("youtube-dl", "--version"))
        ytdltv.setText("Loading...")

        worker.registerWorkerListener(WorkerListener { thread ->
            println("Work done")
            val results = thread.getResults()
            var sb:StringBuilder= StringBuilder()
            val iter: Iterator<*> = results.iterator()
            while (iter.hasNext()) {
                val result = iter.next() as String
                sb.append(result)
            }
           runOnUiThread(Runnable {
                   ytdltv.setText(sb.toString().trim())
           })
        })

        val thread = Thread(worker)
        thread.start()

    }
}