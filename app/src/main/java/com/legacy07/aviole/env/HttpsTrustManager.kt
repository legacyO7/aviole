package com.legacy07.aviole.env

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver
import org.codehaus.plexus.logging.console.ConsoleLoggerManager
import java.io.File
import java.io.IOException
import java.lang.StringBuilder
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.X509Certificate

var sb:StringBuilder?=null
fun logger(string: String){
    Log.d(" output :> ", string)
    sb?.append(String())
}

@Throws(IOException::class)
fun execCmd(cmd: String?): String? {
    val s = Scanner(Runtime.getRuntime().exec(cmd).inputStream)
    return if (s.hasNext()) s.next() else "got nothing"
}

fun extractTar(sourceFile: File, destDir: File):Boolean{
    val ua = TarGZipUnArchiver()
    var manager:ConsoleLoggerManager = ConsoleLoggerManager()
    ua.enableLogging(manager.getLoggerForComponent("bla"))
    ua.sourceFile = sourceFile
    ua.destDirectory = destDir
    ua.extract()
    logger("extracted")
    return true
}

fun changePermission(path: File){

    val perms: MutableSet<PosixFilePermission> = HashSet()
    perms.add(PosixFilePermission.OWNER_READ)
    perms.add(PosixFilePermission.OWNER_WRITE)
    perms.add(PosixFilePermission.OWNER_EXECUTE)

    perms.add(PosixFilePermission.OTHERS_READ)
    perms.add(PosixFilePermission.OTHERS_WRITE)
    perms.add(PosixFilePermission.OTHERS_EXECUTE)

    perms.add(PosixFilePermission.GROUP_READ)
    perms.add(PosixFilePermission.GROUP_WRITE)
    perms.add(PosixFilePermission.GROUP_EXECUTE)

    Files.setPosixFilePermissions(path.toPath(), perms)
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.runCommand(workingDir: File): String? {
    return try {
        val parts = this.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
       logger(proc.errorStream.bufferedReader().readText())
        proc.waitFor(5, TimeUnit.SECONDS)
        proc.inputStream.bufferedReader().readText()
    } catch(e: IOException) {
        e.printStackTrace()
        null
    }
}

class HttpsTrustManager : X509TrustManager {
    @Throws(CertificateException::class)
    fun checkClientTrusted(
        x509Certificates: Array<X509Certificate?>?, s: String?
    ) {
    }

    @Throws(CertificateException::class)
    fun checkServerTrusted(
        x509Certificates: Array<X509Certificate?>?, s: String?
    ) {
    }

    fun isClientTrusted(chain: Array<X509Certificate?>?): Boolean {
        return true
    }

    fun isServerTrusted(chain: Array<X509Certificate?>?): Boolean {
        return true
    }

    val acceptedIssuers: Array<Any>
        get() = this.acceptedIssuers

    companion object {
        private var trustManagers: Array<TrustManager>? = null
        private val acceptedIssuers: Array<X509Certificate> = arrayOf<X509Certificate>()
        fun allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 -> true }
            var context: SSLContext? = null
            if (trustManagers == null) {
                trustManagers = arrayOf(HttpsTrustManager())
            }
            try {
                context = SSLContext.getInstance("TLS")
                context.init(null, trustManagers, SecureRandom())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            }
            HttpsURLConnection.setDefaultSSLSocketFactory(if (context != null) context.getSocketFactory() else null)
        }
    }

    override fun checkClientTrusted(
        chain: Array<out java.security.cert.X509Certificate>?,
        authType: String?
    ) {
    }

    override fun checkServerTrusted(
        chain: Array<out java.security.cert.X509Certificate>?,
        authType: String?
    ) {
        }

    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
        TODO("Not yet implemented")
    }
}