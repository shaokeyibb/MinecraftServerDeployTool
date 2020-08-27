package kim.minecraft.minecraftserverdeploytool.utils

import com.alibaba.fastjson.JSONObject

class ServerJarsUtil(private val type: String) {

    private val latest: JSONObject = JSONObject.parseObject(IOUtil.doSimpleGet("https://serverjars.com/api/fetchLatest/$type"))

    fun download(version: String, saveDir: String, fileName: String) {
        IOUtil.downloadByNIO2("https://serverjars.com/api/fetchJar/$type/$version", saveDir, fileName)
    }

    fun getDownloadFileName(version: String): String {
        val json = JSONObject.parseObject(IOUtil.doSimpleGet("https://serverjars.com/api/fetchAll/$type")).getJSONArray("response")
            .first { JSONObject.parseObject(it.toString()).getString("version") == version } as JSONObject
        return json.getString("file")
    }

    fun download(saveDir: String, fileName: String) {
        IOUtil.downloadByNIO2("https://serverjars.com/api/fetchJar/$type", saveDir, fileName)
    }

    fun getLatestDownloadMinecraftVersion(): String {
        return latest.getJSONObject("response").getString("version")
    }

    private fun getLatestDownloadFileName(): String {
        return latest.getJSONObject("response").getString("file")
    }

}