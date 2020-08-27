package kim.minecraft.minecraftserverdeploytool.tasks

import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil

class VanillaDeploy(
    private val version: String,
    private val saveDir: String,
    private val fileName: String?,
    private val fastMode: BMCLAPIUtil.Src
) : Task {
    override fun run(): String {
        val bmclapi = BMCLAPIUtil(fastMode)
        println("开始从 $fastMode 镜像源加速下载Vanilla，版本$version")
        bmclapi.downloadServer(version, saveDir, fileName)
        println("下载完成")
        return bmclapi.getOriginalServerFileName(version)
    }
}