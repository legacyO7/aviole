package com.legacy07.aviole.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.legacy07.aviole.R
import com.legacy07.aviole.env.logger
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.util.regex.Matcher
import java.util.regex.Pattern


class avHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_av_home)

        val urltext = findViewById<EditText>(R.id.urlinput)
        val urlbutton = findViewById<Button>(R.id.url_button)
        val vView = findViewById<LinearLayout>(R.id.vview)
        val videoView = findViewById<YouTubePlayerView>(R.id.videoview)

        vView.visibility = View.GONE

        urlbutton.setOnClickListener {
            if (urltext.text.toString().trim() != "") {
                val uri: Uri = Uri.parse(urltext.text.toString().trim())
                getLifecycle().addObserver(videoView);




            }
        }
        videoView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(extractYTId(urltext.text.toString())!!, 0f)
                vView.visibility = View.VISIBLE
            }
        })

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