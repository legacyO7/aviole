package com.legacy07.ytcdl

import android.util.Log
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.CertificateException
import javax.net.ssl.*
import javax.security.cert.X509Certificate

fun logger(string: String){
    Log.d(" output :> ", string)
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