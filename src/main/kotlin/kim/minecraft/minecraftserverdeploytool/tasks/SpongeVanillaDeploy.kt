package kim.minecraft.minecraftserverdeploytool.tasks

import com.alibaba.fastjson.JSONObject
import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil
import kim.minecraft.minecraftserverdeploytool.utils.IOUtil
import java.io.File
import java.io.FileInputStream
import java.nio.charset.Charset
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class SpongeVanillaDeploy(
    private val version: String,
    private val saveDir: String,
    private val fileName: String?,
    private val fastMode: BMCLAPIUtil.Src,
    private val cacheDir: String?
) : Task {
    override fun run(): File {
        println("开始部署SpongeVanilla")
        val bmclapi = BMCLAPIUtil(fastMode)
        println("步骤1/4: 开始从 $fastMode 镜像源加速下载Vanilla，版本$version")
        bmclapi.downloadServer(version, saveDir, null)
        println("下载完成")
        val forgeVersion = bmclapi.getLatestForgeVersion(version)
        val cacheDir = cacheDir ?: "temp"
        println("步骤2/4: 开始从 $fastMode 镜像源加速下载Forge，版本$forgeVersion")
        val installerName = "forge-installer.jar"
        bmclapi.downloadForge(
            version, forgeVersion, null, null,
            saveDir + File.separator + cacheDir, installerName
        )
        println("下载完成")
        println("步骤3/4: 安装Forge")
        val forgeInstaller = File(saveDir + File.separator + cacheDir, installerName)
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
                    IOUtil.getRedirectUrl(builder.toString(), null),
                    (saveDir + File.separator + "libraries" + File.separator + path).substringBeforeLast("/")
                        .replace("/", File.separator),
                    path.substringAfterLast("/")
                )
            }
        }
        println("正在尝试运行Forge安装程序")
        Runtime.getRuntime().exec(
            "java -jar \"${forgeInstaller.absolutePath}\" -installServer", null,
            File(saveDir)
        ).waitFor()
        println("安装完成")
        println("步骤4/4: 下载SpongeVanilla")
        object : GitHubDeploy {
            override val author: String = "SpongePowered"
            override val repo: String = "SpongeVanilla"
            override val name: String = "SpongeVanilla"
            override val saveDir: String = this@SpongeVanillaDeploy.saveDir + File.separator + "mods"
            override val fileName: String? = null
            override val index: Int? = 0
        }.run()
        println("清理临时文件")
        File(saveDir, cacheDir).deleteRecursively()
        File(saveDir, "$installerName.log").delete()
        val end = File(saveDir, "forge-$version-$forgeVersion.jar")
        if (!end.exists()) throw Exception("指定服务端文件不存在")
        return if (fileName != null) {
            end.renameTo(File(saveDir, fileName))
            File(saveDir, fileName)
        } else {
            end
        }
    }
}