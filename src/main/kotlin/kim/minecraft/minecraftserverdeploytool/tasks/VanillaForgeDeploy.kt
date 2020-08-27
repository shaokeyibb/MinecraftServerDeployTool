package kim.minecraft.minecraftserverdeploytool.tasks

import com.alibaba.fastjson.JSONObject
import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil
import kim.minecraft.minecraftserverdeploytool.utils.IOUtil
import kim.minecraft.minecraftserverdeploytool.utils.IOUtil.deleteFile
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class VanillaForgeDeploy(
    private val version: String,
    private val saveDir: String,
    private val fileName: String?,
    private val fastMode: BMCLAPIUtil.Src,
    private val cacheDir: String?
) : Task {
    override fun run() {
        println("开始部署VanillaForge")
        val bmclapi = BMCLAPIUtil(fastMode)
        println("步骤1/3: 开始从 $fastMode 镜像源加速下载Vanilla，版本$version")
        bmclapi.downloadServer(version, saveDir, fileName)
        println("下载完成")
        val forgeVersion = bmclapi.getLatestForgeVersion(version)
        val cacheDir = cacheDir ?: "temp"
        println("步骤2/3: 开始从 $fastMode 镜像源加速下载Forge，版本$forgeVersion")
        bmclapi.downloadForge(
            version, forgeVersion, null, null,
            saveDir + File.separator + cacheDir, "forge-installer.jar"
        )
        println("下载完成")
        println("步骤3/3: 安装Forge")
        val forgeInstaller = File(saveDir + File.separator + cacheDir, "forge-installer.jar")
        val zip = ZipInputStream(FileInputStream(forgeInstaller), Charset.forName("UTF-8"))
        var zipEntry = zip.nextEntry
        while (zipEntry != null) {
            if (zipEntry.name == "version.json") break
            zipEntry = zip.nextEntry
        }
        val librariesList = JSONObject.parseObject(
            ZipFile(forgeInstaller).getInputStream(zipEntry).readBytes().toString(Charset.forName("UTF-8"))
        ).getJSONArray("libraries").toList()
        librariesList.iterator().forEach {
            val lib = it as JSONObject
            val url = lib.getJSONObject("downloads").getJSONObject("artifact").getString("url")
            if (url != "") {
                val builder = StringBuilder()
                val path = lib.getJSONObject("downloads").getJSONObject("artifact").getString("path")
                builder.append(fastMode.link).append("/maven/").append(path)
                println("正在从 $fastMode 镜像源加速下载资源文件 ${lib.getString("name")}")
                IOUtil.downloadByNIO2(
                    builder.toString(),
                    (saveDir + File.separator + "libraries" + File.separator + path).substringBeforeLast("/")
                        .replace("/", File.separator),
                    path.substringAfterLast("/")
                )
            }
        }
        Runtime.getRuntime().exec(
            "java -jar \"${forgeInstaller.absolutePath}\" -installServer", null,
            File(saveDir)
        )
        forgeInstaller.parentFile.deleteFile()
        File(saveDir,"forge-installer.jar.log").delete()
        println("安装完成")
    }
}