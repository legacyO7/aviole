package com.legacy07.aviole

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.legacy07.aviole.env.downloadYtdl
import com.legacy07.aviole.env.extractTar
import com.legacy07.aviole.ui.avHome
import java.io.File


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

class MainActivity : AppCompatActivity() {
    val RECORD_REQUEST_CODE = 101
    var mProgressDialog: ProgressDialog? = null
    var alertDialogBuilder: AlertDialog.Builder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appPath = packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir;
        tarPath = "$appPath/miraklebox.tar.gz";
        ytdlPath = "$appPath/youtube-dl";
        homePath = "$appPath/files/home";
        prefixPath = "$appPath/files/usr";

        mProgressDialog = ProgressDialog(this)
        alertDialogBuilder = AlertDialog.Builder(this)

        val w_permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val r_permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (w_permission != PackageManager.PERMISSION_GRANTED && r_permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else
            checkBlobs()

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        setContentView(R.layout.activity_main)

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            RECORD_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    alertDialogBuilder?.setTitle("Gib Permission")
                        ?.setPositiveButton(android.R.string.yes) { dialog, which ->
                            makeRequest()
                        }?.setCancelable(false)?.show()

                } else {
                    checkBlobs()
                }
            }
        }
    }

    fun checkBlobs() {

        val ytdl_bin: File = File(ytdlPath)

        if (!ytdl_bin.exists()) {
            mProgressDialog?.setMessage("Downloading youtube-dl binary")
            mProgressDialog?.setIndeterminate(true);
            mProgressDialog?.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog?.setCancelable(true);

            val downloadFile = downloadYtdl(this, appPath, mProgressDialog!!)
            downloadFile.execute("https://yt-dl.org/downloads/latest/youtube-dl")

            mProgressDialog?.setOnCancelListener {
                downloadFile.cancel(true) //cancel the task
            }
        }
        if (!File(prefixPath).exists()) {
            if (File(tarPath).exists()) {
                mProgressDialog?.setMessage("Extracting aviole module")
                mProgressDialog?.setIndeterminate(true);
                mProgressDialog?.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                extractTar(File(tarPath), File(appPath))
                mProgressDialog?.cancel()
            }
        }
        if(File(prefixPath).exists()&&ytdl_bin.exists()){
            startActivity(Intent(applicationContext,avHome::class.java))
            finish()
        }

    }

}