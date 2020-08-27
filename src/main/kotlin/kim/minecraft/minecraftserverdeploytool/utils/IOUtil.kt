package kim.minecraft.minecraftserverdeploytool.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


object IOUtil {

    fun downloadByNIO2(url: String, saveDir: String, fileName: String) {
        URL(url).openStream().use { ins ->
            val target = Paths.get(saveDir, fileName)
            Files.createDirectories(target.parent)
            Files.copy(ins, target, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun downloadByHTTPConn(url: String, saveDir: String, fileName: String, requestProperty: Map<String, String>?) {
        val builder = StringBuilder()
        var count = 0
        requestProperty?.asIterable()?.forEach {
            if (count == 0) builder.append("?") else builder.append("&")
            builder.append(it.key + "=" + it.value)
            count++
        }
        val u = URL(url + builder.toString())
        val conn = u.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.instanceFollowRedirects = true
        conn.connectTimeout = 5000
        conn.setRequestProperty(
            "User-agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36"
        )
            conn.inputStream.buffered().use { inputStream ->
                FileOutputStream(
                    File(
                        saveDir,
                        fileName
                    ).also {
                        if (!it.parentFile.exists())
                            it.parentFile.mkdir()
                        if (!it.exists())
                            it.createNewFile()
                    }).use { fileOutputStream ->
                    val dataBuffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(dataBuffer, 0, 1024).also { bytesRead = it } != -1) {
                        fileOutputStream.write(dataBuffer, 0, bytesRead)
                    }
                }
            }
    }

    fun doSimpleGet(url: String): String {
        return URL(url).readText()
    }

    @Deprecated(message = "doGet()和downloadByHTTPConn()均已支持自动重定向，无需再使用本函数", level = DeprecationLevel.WARNING)
    fun getRedirectUrl(url: String): String {
        val u = URL(url)
        val conn = u.openConnection() as HttpURLConnection
        conn.instanceFollowRedirects = false
        conn.connectTimeout = 5000
        val redirect = conn.getHeaderField("Location")
        return if (redirect != null) {
            if (!redirect.startsWith("http", true)) {
                getRedirectUrl(u.toURI().resolve(redirect).toURL().toString())
            } else {
                getRedirectUrl(redirect)
            }
        } else {
            u.toString()
        }
    }

    fun doGet(url: String, requestProperty: Map<String, String>?): String {
        val builder = StringBuilder()
        var count = 0
        requestProperty?.asIterable()?.forEach {
            if (count == 0) builder.append("?") else builder.append("&")
            builder.append(it.key + "=" + it.value)
            count++
        }
        val u = URL(url + builder.toString())
        val conn = u.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.doInput = true
        conn.instanceFollowRedirects = true
        conn.connectTimeout = 5000
        conn.setRequestProperty(
            "User-agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36"
        )
        return conn.inputStream.bufferedReader().use(BufferedReader::readText)
    }

    fun File.deleteFile(): Boolean {
        if (!this.exists()) {
            return false
        }
        if (this.isFile) {
            return this.delete()
        } else {
            for (file in this.listFiles()!!) {
                file.deleteFile()
            }
        }
        return this.delete()
    }

}