package kim.minecraft.minecraftserverdeploytool.utils

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

class GitHubUtil(author: String, repo: String) {

    private val latestJson: JSONObject =
        JSONObject.parseObject(IOUtil.doSimpleGet("https://api.github.com/repos/$author/$repo/releases/latest"))

    private val json: JSONArray =
        JSONArray.parseArray(IOUtil.doSimpleGet("https://api.github.com/repos/$author/$repo/releases"))

    private fun getAssetsByTagName(tagName: String, index: Int): JSONObject {
        return (json.first { (it as JSONObject).getString("tag_name") == tagName } as JSONObject).getJSONObject(index.toString())
    }

    fun getReleaseDownloadLink(tagName: String, index: Int): String {
        return getAssetsByTagName(tagName, index).getString("browser_download_url")
    }

    fun getReleaseDownloadFileName(tagName: String, index: Int): String {
        return getAssetsByTagName(tagName, index).getString("name")
    }

    private fun getAssets(index: Int): JSONObject {
        return latestJson.getJSONArray("assets").getJSONObject(index)
    }

    fun getLatestReleaseDownloadLink(index: Int): String {
        return getAssets(index).getString("browser_download_url")
    }

    fun getLatestReleaseDownloadFileName(index: Int): String {
        return getAssets(index).getString("name")
    }

    fun getLatestReleaseTagName(): String {
        return latestJson.getString("tag_name")
    }
}
