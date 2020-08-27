package kim.minecraft.minecraftserverdeploytool.utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

class JenkinsCIUtil(
    private val baseLink: String,
    private val author: String,
    private val repo: String
) {

    private val buildIDs: JSONArray =
        JSONObject.parseObject(IOUtil.doGet("$baseLink/job/$author/job/$repo/api/json?pretty=true", null))
            .getJSONArray("builds")

    fun getLatestBuild(): String {
        return buildIDs.getJSONObject(0).getString("number")
    }

    private fun getArtifacts(): JSONArray {
        return JSONObject.parseObject(
            IOUtil.doGet(
                "$baseLink/job/$author/job/$repo/lastSuccessfulBuild/api/json?pretty=true",
                null
            )
        )
            .getJSONArray("artifacts")
    }

    private fun getArtifacts(buildID: String): JSONArray {
        return JSONObject.parseObject(
            IOUtil.doGet(
                "$baseLink/job/$author/job/$repo/$buildID/api/json?pretty=true",
                null
            )
        ).getJSONArray("artifacts")
    }

    fun getFileName(index: Int, buildID: String) = getArtifacts(buildID).getJSONObject(index).getString("fileName")

    fun getFileName(index: Int) = getArtifacts().getJSONObject(index).getString("fileName")

    fun download(index: Int, saveDir: String) {
        val artifacts = getArtifacts()
        IOUtil.downloadByHTTPConn(
            "$baseLink/job/$author/job/$repo/lastSuccessfulBuild/artifact/" + artifacts.getJSONObject(
                index
            ).getString("relativePath"), saveDir, getFileName(index), null
        )
    }

    fun download(index: Int, buildID: String, saveDir: String) {
        val artifacts = getArtifacts(buildID)
        IOUtil.downloadByHTTPConn(
            "$baseLink/job/$author/job/$repo/$buildID/artifact/" + artifacts.getJSONObject(
                index
            ).getString("relativePath"), getFileName(index, buildID), saveDir
            , null
        )
    }

    fun download(index: Int, buildID: String, saveDir: String, fileName: String) {
        val artifacts = getArtifacts(buildID)
        IOUtil.downloadByHTTPConn(
            "$baseLink/job/$author/job/$repo/$buildID/artifact/" + artifacts.getJSONObject(
                index
            ).getString("relativePath"), saveDir, fileName
            , null
        )
    }

}