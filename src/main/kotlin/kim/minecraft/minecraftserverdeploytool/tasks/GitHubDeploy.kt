package kim.minecraft.minecraftserverdeploytool.tasks

import kim.minecraft.minecraftserverdeploytool.utils.GitHubUtil
import kim.minecraft.minecraftserverdeploytool.utils.IOUtil
import java.io.File

interface GitHubDeploy : Task {
    val author: String
    val repo: String
    val name: String
    val saveDir: String
    val fileName: String?
    val index: Int?
    override fun run(): File {
        val github = GitHubUtil(author, repo)
        val index = index ?: 0
        val fileName = fileName ?: github.getLatestReleaseDownloadFileName(index)
        println("开始下载$name，版本" + github.getLatestReleaseTagName())
        IOUtil.downloadByHTTPConn(github.getLatestReleaseDownloadLink(index), saveDir, fileName, null)
        println("下载完成")
        additionAction()
        return File(saveDir, fileName)
    }

    fun additionAction() {}
}
