package kim.minecraft.minecraftserverdeploytool.tasks

import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil
import kim.minecraft.minecraftserverdeploytool.utils.PaperMCUtil
import java.io.File

class PaperDeploy(
    private val version: String?,
    private val buildID: String?,
    private val saveDir: String,
    private val fileName: String?,
    private val fastMode: BMCLAPIUtil.Src?
) :
    Task {

    override fun run() {
        val paper = PaperMCUtil(PaperMCUtil.Project.PAPER)
        val version = version ?: paper.latestMinecraftVersion
        val buildID = buildID ?: paper.getLatestBuild(version)
        val fileName = fileName ?: paper.getFileName(buildID)
        println("开始下载Paper，版本$version，构建版本$buildID")
        paper.download(version, buildID, saveDir, fileName)
        println("下载完成")
        if (fastMode != null) {
            println("正在从 $fastMode 镜像源加速下载资源文件")
            BMCLAPIUtil(fastMode).downloadServer(version, File(saveDir, "cache").absolutePath, "mojang_$version.jar")
            println("下载完成")
        }
    }

}
