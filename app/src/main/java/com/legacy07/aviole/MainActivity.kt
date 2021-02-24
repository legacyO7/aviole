package com.legacy07.aviole

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.legacy07.aviole.env.*
import com.legacy07.aviole.misc.*
import com.legacy07.aviole.ui.avHome
import java.io.File

class MainActivity : AppCompatActivity() {
    val RECORD_REQUEST_CODE = 101
    var mProgressDialog: ProgressDialog? = null
    var alertDialogBuilder: AlertDialog.Builder? = null

    var setupbutton: Button?=null
    override fun onStart() {
        super.onStart()
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appPath = packageManager.getPackageInfo(packageName, 0).applicationInfo.dataDir;
        tarPath = "$appPath/avioleTPeM.tar.gz";
        ytdlPath = "$appPath/youtube-dl";
        homePath = "$appPath/files/home";
        prefixPath = "$appPath/files/usr";

        mProgressDialog = ProgressDialog(this)
        alertDialogBuilder = AlertDialog.Builder(this)


        setupbutton=findViewById(R.id.setup_button)


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
          initYTDLdownload(this,mProgressDialog!!)
        }
        if (!File(prefixPath).exists()) {
            if (File(tarPath).exists()) {
                extract_aviole_tarball(this,mProgressDialog!!).execute()
            }else {
              initAvioleModuleDownload(this,mProgressDialog!!)
            }
        }
        if(File(prefixPath).exists()&&ytdl_bin.exists()){
            startActivity(Intent(applicationContext,avHome::class.java))
            finish()
        }else
        {
            setupbutton?.visibility= View.VISIBLE
            setupbutton?.setOnClickListener { checkBlobs() }
        }

    }

}