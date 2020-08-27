package kim.minecraft.minecraftserverdeploytool.tasks

import kim.minecraft.minecraftserverdeploytool.utils.ServerJarsUtil

interface ServerJarsDeploy : Task {
    val type: String
    val name: String
    val version: String?
    val saveDir: String
    val fileName: String?

    override fun run(): String {
        val mirror = ServerJarsUtil(type)
        val version = version ?: mirror.getLatestDownloadMinecraftVersion()
        val fileName = fileName ?: mirror.getDownloadFileName(version)
        println("开始下载$name，版本$version")
        mirror.download(version, saveDir, fileName)
        println("下载完成")
        additionAction()
        return fileName
    }

    fun additionAction() {}
}