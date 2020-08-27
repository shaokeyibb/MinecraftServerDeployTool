package kim.minecraft.minecraftserverdeploytool.utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

class LegacyJenkinsCIUtil(
    private val baseLink: String,
    private val repo: String
) {

    private val buildIDs: JSONArray =
        JSONObject.parseObject(IOUtil.doGet("$baseLink/job/$repo/api/json?pretty=true", null))
            .getJSONArray("builds")

    fun getLatestBuild(): String {
        return buildIDs.getJSONObject(0).getString("number")
    }

    private fun getArtifacts(): JSONArray {
        return JSONObject.parseObject(
            IOUtil.doGet(
                "$baseLink//job/$repo/lastSuccessfulBuild/api/json?pretty=true",
                null
            )
        )
            .getJSONArray("artifacts")
    }

    private fun getArtifacts(buildID: String): JSONArray {
        return JSONObject.parseObject(
            IOUtil.doGet(
                "$baseLink//job/$repo/$buildID/api/json?pretty=true",
                null
            )
        )
            .getJSONArray("artifacts")
    }

    fun getFileName(index: Int, buildID: String): String =
        getArtifacts(buildID).getJSONObject(index).getString("fileName")

    fun getFileName(index: Int): String = getArtifacts().getJSONObject(index).getString("fileName")

    fun download(index: Int, saveDir: String) {
        val artifacts = getArtifacts()
        IOUtil.downloadByHTTPConn(
            "$baseLink//job/$repo/lastSuccessfulBuild/artifact/" + artifacts.getJSONObject(
                index
            ).getString("relativePath"), saveDir, getFileName(index), null
        )
    }

    fun download(index: Int, buildID: String, saveDir: String) {
        val artifacts = getArtifacts(buildID)
        IOUtil.downloadByHTTPConn(
            "$baseLink/job/$repo/$buildID/artifact/" + artifacts.getJSONObject(
                index
            ).getString("relativePath"), getFileName(index, buildID), saveDir, null
        )
    }

    fun download(index: Int, buildID: String, saveDir: String, fileName: String) {
        val artifacts = getArtifacts(buildID)
        IOUtil.downloadByHTTPConn(
            "$baseLink/job/$repo/$buildID/artifact/" + artifacts.getJSONObject(
                index
            ).getString("relativePath"), saveDir, fileName, null
        )
    }
}