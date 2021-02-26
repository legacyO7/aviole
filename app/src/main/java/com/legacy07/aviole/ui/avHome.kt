package com.legacy07.aviole.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.legacy07.aviole.R
import com.legacy07.aviole.env.logger
import com.legacy07.aviole.misc.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern

//esac; \
//_PYTHON_PROJECT_BASE=/home/legacy07/.termux-build/python/build _PYTHON_HOST_PLATFORM=linux-aarch64 PYTHONPATH=/home/legacy07/.termux-build/python/build/build/lib.linux-aarch64-3.9:/home/legacy07/.termux-build/python/src/Lib _PYTHON_SYSCONFIGDATA_NAME=_sysconfigdata__linux_ python3.9 -m ensurepip \
//$ensurepip --root=/ ; \

class avHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_av_home)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val urltext = findViewById<EditText>(R.id.urlinput)
        val urlbutton = findViewById<Button>(R.id.url_button)
        val vView = findViewById<LinearLayout>(R.id.vview)
        val videoView = findViewById<YouTubePlayerView>(R.id.videoview)
        val activity:Activity=this

        vView.visibility = View.GONE
        val tvtv=findViewById<TextView>(R.id.tvtv)



       // (File("$appPath/ffprobe").setExecutable(true))
      // changePermission(File("$appPath/ffprobe"))
      //  executeToTextView(activity,tvtv,"youtube-dl", avCommand("-v -x --audio-format mp3 --ffmpeg-location /data/data/com.legacy07.aviole/files/usr/bin --ignore-config https://youtu.be/Uulu0pnaK70"))
        executeToTextView(activity,tvtv, avCommand("pkg|install python -y"))

        urlbutton.setOnClickListener {
            urltext.setText("blahhh")
            if (urltext.text.toString().trim() != "") {
                val uri: Uri = Uri.parse(urltext.text.toString().trim())
                getLifecycle().addObserver(videoView);

                if (urltext.text.toString().trim()!="null")
                vView.visibility = View.VISIBLE


            }
        }

    }

    //setting menu in action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menubar,menu)
        return super.onCreateOptionsMenu(menu)
    }

    // actions on click menu items
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.info -> {
           startActivity(Intent(applicationContext,avInfo::class.java))
            true
        }
        android.R.id.home -> {
            Toast.makeText(this, "Home action", Toast.LENGTH_LONG).show()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
    fun extractYTId(ytUrl: String): String? {
        var vId: String? = null
        val pattern: Pattern = Pattern.compile(
            "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
            Pattern.CASE_INSENSITIVE
        )
        val matcher: Matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            vId = matcher.group(1)
        }
        logger(vId.toString())
        return vId
    }


}