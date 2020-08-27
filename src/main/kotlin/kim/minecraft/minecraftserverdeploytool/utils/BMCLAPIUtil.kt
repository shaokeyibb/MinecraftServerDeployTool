package kim.minecraft.minecraftserverdeploytool.utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

class BMCLAPIUtil(private val source: Src) {

    private fun getServerDownloadLink(version: String): String =
        source.link + "/version/$version/server"

    private fun getForgeList(mcversion: String): JSONArray =
        JSONArray.parseArray(IOUtil.doSimpleGet(source.link + "/forge/minecraft/$mcversion"))

    fun getLatestForgeVersion(mcVersion: String): String {
        val list = getForgeList(mcVersion)
        list.sortByDescending {
            val var0 = it as JSONObject
            var0.getString("version").replace('.', '0').toLong()
        }
        return list.getJSONObject(0).getString("version")

    }

    fun downloadForge(
        mcVersion: String,
        version: String?,
        category: String?,
        format: String?,
        saveDir: String,
        fileName: String?
    ) {
        val ver = version ?: getLatestForgeVersion(mcVersion)
        val cat = category ?: "installer"
        val form = format ?: "jar"
        IOUtil.downloadByHTTPConn(
            IOUtil.getRedirectUrl(
                source.link + "/forge/download", mapOf(
                    Pair("mcversion", mcVersion),
                    Pair("version", ver),
                    Pair("category", cat),
                    Pair("format", form)
                )
            ),
            saveDir,
            fileName ?: getOriginalForgeFileName(mcVersion, ver, cat, form),
            null
        )
    }

    private fun getOriginalForgeFileName(
        mcVersion: String,
        version: String,
        category: String,
        format: String
    ) = "forge-$mcVersion-$version-$category.$format"

    fun getOriginalServerFileName(version: String): String = "minecraft_server.$version.jar"

    fun downloadServer(version: String, saveDir: String, fileName: String?) =
        IOUtil.downloadByHTTPConn(
            IOUtil.getRedirectUrl(getServerDownloadLink(version), null),
            saveDir,
            fileName ?: getOriginalServerFileName(version),
            null
        )

    enum class Src(val link: String) {
        ORIGINAL("https://bmclapi2.bangbang93.com"), MCBBS("https://download.mcbbs.net")
    }
}