package kim.minecraft.minecraftserverdeploytool.tasks

import kim.minecraft.minecraftserverdeploytool.utils.GithubUtil
import kim.minecraft.minecraftserverdeploytool.utils.IOUtil
import net.lingala.zip4j.ZipFile
import java.io.File

class ContigoDeploy(
    override val saveDir: String,
    override val fileName: String?

) : GithubDeploy {
    override val author: String = "djoveryde"
    override val repo: String = "Contigo"
    override val name: String = "Contigo"
    override val index: Int? = 0
    override fun additionAction() {
        val github = GithubUtil("djoveryde","Contigo")
        val ind = 1
        val fileNa = github.getLatestReleaseDownloadFileName(ind)
        println("开始下载资源文件，版本${github.getLatestReleaseTagName()}")
        IOUtil.downloadByNIO2(github.getLatestReleaseDownloadLink(ind),saveDir,fileNa)
        val file = File(saveDir, fileNa)
        println("下载完成")
        println("开始解压资源文件...")
        ZipFile(file).extractAll(saveDir)
        println("解压完成")
        file.delete()
    }
}