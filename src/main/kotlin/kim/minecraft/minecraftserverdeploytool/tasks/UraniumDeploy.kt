package kim.minecraft.minecraftserverdeploytool.tasks

import kim.minecraft.minecraftserverdeploytool.utils.LegacyJenkinsCIUtil
import net.lingala.zip4j.ZipFile
import java.io.File

class UraniumDeploy(
    override val buildID: String?,
    override val saveDir: String,
    override val fileName: String?
) : LegacyJenkinsCIDeploy {
    override val baseLink: String = "https://ci.uraniummc.cc/"
    override val repo: String = "Uranium-dev"
    override val name: String = "Uranium"
    override val index: Int? = 2

    override fun additionAction() {
        val ci = LegacyJenkinsCIUtil(baseLink, repo)
        val ind = 1
        val buildID = "271"
        val fileNa = ci.getFileName(ind, buildID)
        println("开始下载资源文件，版本$buildID")
        ci.download(ind, buildID, saveDir, fileNa)
        val file = File(saveDir, fileNa)
        println("下载完成")
        println("开始解压资源文件...")
        ZipFile(file).extractAll(saveDir)
        println("解压完成")
        file.delete()
        File(saveDir, "kBootstrapX.reposList").delete()
    }
}