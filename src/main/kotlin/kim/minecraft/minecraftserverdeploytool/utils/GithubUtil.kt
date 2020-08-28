package kim.minecraft.minecraftserverdeploytool.utils

import com.alibaba.fastjson.JSONObject

class GitHubUtil(author: String, repo: String) {

    private val json: JSONObject =
        JSONObject.parseObject(IOUtil.doSimpleGet("https://api.github.com/repos/$author/$repo/releases/latest"))

    private fun getAssets(index: Int): JSONObject {
        return json.getJSONArray("assets").getJSONObject(index)
    }

    fun getLatestReleaseDownloadLink(index: Int): String{
        return getAssets(index).getString("browser_download_url")
    }

    fun getLatestReleaseDownloadFileName(index: Int): String {
        return getAssets(index).getString("name")
    }

    fun getLatestReleaseTagName():String{
        return json.getString("tag_name")
    }
}