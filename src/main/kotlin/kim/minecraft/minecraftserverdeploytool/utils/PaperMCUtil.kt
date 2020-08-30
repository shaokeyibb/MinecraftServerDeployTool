package kim.minecraft.minecraftserverdeploytool.utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

class PaperMCUtil(project: Project) {

    private val original: String = "https://papermc.io/api/v1/" + project.realName
    val latestMinecraftVersion: String = getAllMinecraftVersions().getString(0)

    fun getFileName(buildID: String) = "paper-$buildID.jar"

    fun getAllMinecraftVersions(): JSONArray {
        return JSONObject.parseObject(IOUtil.doSimpleGet(original)).getJSONArray("versions")
    }

    private fun getBuilds(version: String): JSONObject {
        return JSONObject.parseObject(IOUtil.doSimpleGet("$original/$version")).getJSONObject("builds")
    }

    fun getBuildIDs(version: String): JSONArray {
        return getBuilds(version).getJSONArray("all")
    }

    fun getLatestBuild(version: String): String {
        return getBuilds(version).getString("latest")
    }

    fun download(saveDir: String) = download(saveDir, getFileName(getLatestBuild(latestMinecraftVersion)))

    private fun download(saveDir: String, fileName: String) {
        IOUtil.downloadByNIO2("$original/$latestMinecraftVersion/latest/download", saveDir, fileName)
    }

    fun download(version: String, saveDir: String, fileName: String) {
        IOUtil.downloadByNIO2("$original/$version/latest/download", saveDir, fileName)
    }

    fun download(version: String, buildID: String, saveDir: String, fileName: String) {
        IOUtil.downloadByNIO2("$original/$version/$buildID/download", saveDir, fileName)
    }

    enum class Project(val realName: String) {
        PAPER("paper")
    }
}
