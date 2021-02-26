package com.legacy07.aviole.misc

import android.os.Environment

@JvmField
var outputText: String? = ""

@JvmField
var appPath: String = ""

@JvmField
var tarPath: String = ""

@JvmField
var ytdlPath: String = ""

@JvmField
var homePath: String = ""

@JvmField
var prefixPath: String = ""

@JvmField
//var aviole_module_URL="https://raw.githubusercontent.com/legacyO7/scheduler/master/README.md"
var aviole_module_URL="https://github.com/legacyO7/aviole/raw/master/aviole_tarball_releases/aviole.tar.gz"

@JvmField
var ytdl_url="https://yt-dl.org/downloads/latest/youtube-dl"

var outputFileLocation="-o ${Environment.getExternalStorageDirectory()}/${Environment.DIRECTORY_MUSIC}/%(title)s.%(ext)s"