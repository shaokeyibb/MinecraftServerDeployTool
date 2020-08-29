package kim.minecraft.minecraftserverdeploytool.tasks

import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil
import java.io.File

class VanillaFabricDeploy(
    private val version: String,
    private val saveDir: String,
    private val fileName: String?,
    private val fastMode: BMCLAPIUtil.Src?,
    private val cacheDir: String?
    //,private val installFabricAPI: Boolean,
    //private val installCarpet: Boolean
) : Task {
    override fun run(): File {
        println("开始部署VanillaFabric")
        println("步骤1/3: 开始下载FabricInstaller")
        val cacheDir = cacheDir ?: "temp"
        val fabricInstaller = object : JenkinsCIDeploy {
            override val baseLink: String = "https://jenkins.modmuss50.me"
            override val author: String = "FabricMC"
            override val repo: String = "fabric-installer/job/master"
            override val name: String = "FabricLoader"
            override val buildID: String? = null
            override val saveDir: String = this@VanillaFabricDeploy.saveDir + File.separator + cacheDir
            override val fileName: String? = null
            override val index: Int? = null
        }.runTask()
        println("下载完成")
        if (fastMode != null) {
            val bmclapi = BMCLAPIUtil(fastMode)
            println("步骤2/3: 开始从 $fastMode 镜像源加速下载Vanilla，版本$version")
            bmclapi.downloadServer(version, saveDir, "server.jar")
            println("下载完成")
        } else {
            println("未开启加速下载模式，跳过Vanilla下载")
        }
        println("步骤3/3: 安装Fabric")
        if (fastMode != null) {
            Runtime.getRuntime().exec(
                //"java -jar \"${fabricInstaller.absolutePath}\" server -snapshot -dir $saveDir -mcversion $version -mavenurl ${fastMode.link + "/maven"} -metaurl ${fastMode.link + "/fabric-meta/"}}",
                "java -jar \"${fabricInstaller.absolutePath}\" server -snapshot -dir $saveDir -mcversion $version -metaurl \"${fastMode.link + "/fabric-meta/"}\"}",
                null,
                File(saveDir)
            ).waitFor()
        } else {
            Runtime.getRuntime().exec(
                "java -jar \"${fabricInstaller.absolutePath}\" server -snapshot -dir $saveDir -mcversion $version -downloadMinecraft",
                null,
                File(saveDir)
            ).waitFor()
        }
        println("安装完成")
        File(saveDir, cacheDir).deleteRecursively()
        val end = File(saveDir, "fabric-server-launch.jar")
        if (!end.exists()) throw Exception("指定服务端文件不存在")
        return if (fileName != null) {
            end.renameTo(File(saveDir, fileName))
            File(saveDir, fileName)
        } else {
            end
        }
    }

}