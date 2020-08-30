package kim.minecraft.minecraftserverdeploytool.guide

import kim.minecraft.minecraftserverdeploytool.Main
import kim.minecraft.minecraftserverdeploytool.tasks.*
import kim.minecraft.minecraftserverdeploytool.utils.*
import kim.minecraft.minecraftserverdeploytool.utils.ConsoleUtil.printf
import kim.minecraft.minecraftserverdeploytool.utils.ConsoleUtil.readLine
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess

object RunningManage {

    private val settings: File = File("MCSDTSettings", "settings.yml")

    private var tempSettings = mutableMapOf<String, String>()

    private fun checkIfFirstRun(): Boolean = !settings.exists()

    private fun firstRunWithTry() {
        try {
            firstRun()
        } catch (e: Exception) {
            e.printStackTrace()
            println("服务端部署失败，因为" + e.localizedMessage)
        }
    }

    private fun checkUpdate() {
        try {
            val latestVersion: String = GitHubUtil("shaokeyibb", "MinecraftServerDeployTool").getLatestReleaseTagName()
            if (latestVersion == Main.version) {
                println("您正在使用最新的发行版本「${Main.version}」，无需更新")
            } else {
                println("您正在使用过时的发行版本「${Main.version}」，当前最新版本是「${latestVersion}」")
            }
        } catch (e: java.lang.Exception) {
            println("由于 ${e.localizedMessage} ，检查更新失败")
        }
    }

    private fun sendBroadcast() {
        try {
            println(IOUtil.doSimpleGet("https://raw.githubusercontent.com/shaokeyibb/MinecraftServerDeployTool/master/broadcast"))
        } catch (e: java.lang.Exception) {
            println("由于 ${e.localizedMessage} ，获取公告失败")
        }
    }

    fun init() {
        checkUpdate()
        sendBroadcast()
        if (checkIfFirstRun()) firstRunWithTry() else commonRun()
    }

    private fun firstRun() {
        printf("检测到第一次运行，启动初始化引导程序......")
        Thread.sleep(2000)
        printf("Hey，欢迎使用MinecraftServerDeployTools，通过本程序，您可以一键快速构建主流服务端环境并立刻开服!")
        Thread.sleep(2000)
        selectServerCore()
        Thread.sleep(2000)
        deployServerCore()
        Thread.sleep(2000)
        setRuntimeArgs()
        Thread.sleep(2000)
        commonRun()
    }

    private fun selectServerCore() {
        val cores = ServersManage.Servers.values().toList().also {
            printf("可部署核心列表: " + it.asSequence()
                .mapIndexed { index, servers -> "[${index + 1}]${servers.name} " }
                .joinToString(", ")
            )
        }
        val coreName = readLine(prefix = "请选择您希望部署的服务端核心名称: ") {
            cores.map { it.name }
        }
        val indexed = coreName.toIntOrNull()?.minus(1)
        if ((coreName !in cores.groupBy { it.name }) && (indexed == null || indexed > ServersManage.Servers.values().size)) {
            printf("输入有误，请检查大小写后重试，或指定服务端不支持部署")
            selectServerCore()
            return
        }
        if (indexed != null) {
            tempSettings["CoreName"] = cores[indexed].name
        } else {
            tempSettings["CoreName"] = ServersManage.Servers.valueOf(coreName).name
        }

        printf("您即将部署服务端 ${tempSettings["CoreName"]}")
    }

    private fun deployServerCore() {
        when (tempSettings["CoreName"]) {
            "Akarin" -> {
                val normalVersion = "1.16.2"
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                    .takeIf { it.isNotEmpty() } ?: normalVersion
                val build = readLine(prefix = "请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                    .takeIf { it.isNotEmpty() }
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ").takeIf { it.isNotEmpty() }
                AkarinDeploy(version, build, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }

            }
            "Arclight" -> {
                val normalVersion = "1.15.2"
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                    .takeIf { it.isNotEmpty() } ?: normalVersion
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                ArclightDeploy(version, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "CatServer" -> {
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                CatServerDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Contigo" -> {
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                ContigoDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "CraftBukkit" -> {
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ")
                    .takeIf { it.isNotEmpty() }
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                CraftBukkitDeploy(version, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Magma" -> {
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                MagmaDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Mohist" -> {
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                MohistDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Paper" -> {
                val paper = PaperMCUtil(PaperMCUtil.Project.PAPER)
                val versions = paper.getAllMinecraftVersions().also { printf("可用游戏版本: " + it.joinToString(", ")) }
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ") {
                    versions.map { it.toString() }
                }.takeIf { it.isNotEmpty() }
                val buildIDs = paper.let { paperMCUtil ->
                    paperMCUtil.getBuildIDs(version ?: paperMCUtil.latestMinecraftVersion)
                }.also { printf("可用构建版本: " + it.joinToString(", ")) }
                val build = readLine(prefix = "请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ") {
                    buildIDs.map { it.toString() }
                }.takeIf { it.isNotEmpty() }
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                val fastModeRaw =
                    readLine(prefix = "是否使用BMCLAPI/MCBBS镜像源下载资源文件，是请填写mcbbs或bmclapi，否请输入no，留空使用MCBBS镜像源: ") {
                        listOf("mcbbs", "bmclapi", "no")
                    }
                val fastMode = when (fastModeRaw) {
                    "mcbbs", "" -> BMCLAPIUtil.Src.MCBBS
                    "bmclapi" -> BMCLAPIUtil.Src.ORIGINAL
                    else -> null
                }
                PaperDeploy(version, build, saveDir, fileName, fastMode).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Spigot" -> {
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ")
                    .takeIf { it.isNotEmpty() }
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                SpigotDeploy(version, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "SpongeForge" -> {
                val normalVersion = "1.12.2"
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                    .takeIf { it.isNotEmpty() } ?: normalVersion
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                val fastModeRaw =
                    readLine(prefix = "请选择使用的镜像源（mcbbs/bmclapi），留空使用MCBBS镜像源: ") { listOf("mcbbs", "bmclapi") }
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = readLine(prefix = "请输入您欲部署的服务端资源临时目录，留空则使用默认名称: ")
                    .takeIf { it.isNotEmpty() }
                SpongeForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "SpongeVanilla" -> {
                val normalVersion = "1.12.2"
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                    .takeIf { it.isNotEmpty() } ?: normalVersion
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                val fastModeRaw =
                    readLine(prefix = "请选择使用的镜像源（mcbbs/bmclapi），留空使用MCBBS镜像源: ") { listOf("mcbbs", "bmclapi") }
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = readLine(prefix = "请输入您欲部署的服务端资源临时目录，留空则使用默认名称: ").takeIf { it.isNotEmpty() }
                SpongeVanillaDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Tuinity" -> {
                val build = readLine(prefix = "请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                    .takeIf { it.isNotEmpty() }
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                TuinityDeploy(build, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Uranium" -> {
                val build = readLine(prefix = "请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                UraniumDeploy(build, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Vanilla" -> {
                val normalVersion = "1.16.2"
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                    .takeIf { it.isNotEmpty() } ?: normalVersion
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                val fastModeRaw =
                    readLine(prefix = "请选择使用的镜像源（mcbbs/bmclapi），留空使用MCBBS镜像源: ") { listOf("mcbbs", "bmclapi") }
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                VanillaDeploy(version, saveDir, fileName, fastMode).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "VanillaFabric" -> {
                val normalVersion = "1.16.2"
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                    .takeIf { it.isNotEmpty() } ?: normalVersion
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                    .takeIf { it.isNotEmpty() } ?: "./"
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                val fastModeRaw = readLine(prefix = "请选择使用的镜像源（mcbbs/bmclapi），留空使用MCBBS镜像源: "){ listOf("mcbbs", "bmclapi") }
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = readLine(prefix = "请输入您欲部署的服务端资源临时目录，留空则使用默认名称: ").takeIf { it.isNotEmpty() }
                VanillaFabricDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "VanillaForge" -> {
                val normalVersion = "1.16.2"
                val version = readLine(prefix = "请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                    .takeIf { it.isNotEmpty() } ?: normalVersion
                val saveDir = readLine(prefix = "请输入您欲部署到的目录，留空即为当前目录: ")
                val fileName = readLine(prefix = "请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                    .takeIf { it.isNotEmpty() }
                val fastModeRaw = readLine(prefix = "请选择使用的镜像源（mcbbs/bmclapi），留空使用MCBBS镜像源: "){ listOf("mcbbs", "bmclapi") }
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = readLine(prefix = "请输入您欲部署的服务端资源临时目录，留空则使用默认名称: ").takeIf { it.isNotEmpty() }
                VanillaForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            else -> {
                throw Exception("未知的服务端名称: ${tempSettings["Corename"]}")
            }
        }
    }

    private fun setRuntimeArgs() {
        printf("接下来，请遵循向导配置您的服务端运行参数")
        val jreLocation = readLine(prefix = "请输入您希望使用的Java运行时环境文件(如java.exe)位置，留空则使用「Java」作为运行时文件位置: ")
            .takeIf { it.isNotEmpty() } ?: "Java"
        tempSettings["JRELocation"] = jreLocation
        printf("参数已被设置为「$jreLocation」")
        val maxRAM = readLine(prefix = "请输入您希望在开服时分配的最大内存，单位MB，留空则使用1024MB: ")
            .takeIf { it.isNotEmpty() && it.toIntOrNull() != null } ?: "1024"
        tempSettings["MaxRAM"] = maxRAM
        printf("参数已被设置为「$maxRAM」")
        val minRAM = readLine(prefix = "请输入您希望在开服时分配的最小内存，单位MB，留空则与最大内存对等: ")
            .takeIf { it.isNotEmpty() && it.toIntOrNull() != null } ?: maxRAM
        tempSettings["MinRAM"] = minRAM
        printf("参数已被设置为「$minRAM」")
        val firstArgs = readLine(prefix = "请输入您希望加载的Java额外命令行前置参数(JVM参数)，留空则不使用额外参数: ")
        tempSettings["FirstArgs"] = firstArgs
        printf("参数已被设置为「$firstArgs」")
        val lastArgs = readLine(prefix = "请输入您希望加载的Java额外命令行后置参数(服务端参数)，留空则不使用额外参数: ")
            .takeIf { it.isNotEmpty() } ?: ""
        printf("参数已被设置为「$lastArgs」")
        tempSettings["LastArgs"] = lastArgs
        printf("运行参数设置完毕，保存配置中")
        saveSettings()
        printf("配置文件已保存，您随时可在 ${settings.canonicalPath} 位置找到此配置文件")
    }

    private fun isNotAgreeEULA(): Boolean {
        val eula = File(tempSettings["CoreFileSavedDir"], "eula.txt")
        return if (eula.exists()) {
            Properties().apply { this.load(eula.inputStream()) }.getProperty("eula") == "false"
        } else {
            true
        }
    }

    private fun agreeEULA(): Boolean {
        val eula = File(tempSettings["CoreFileSavedDir"], "eula.txt")
        return if (isNotAgreeEULA()) {
            printf("开服前，您需要同意Minecraft软件用户最终许可协议(EULA)，您可前往 https://account.mojang.com/documents/minecraft_eula 获取EULA全文")
            // No tab here.
            if (readLine(prefix = "如果您同意EULA，请输入「agree」: ") == "agree") {
                if (!eula.exists()) eula.createNewFile()
                Properties().also {
                    it.load(eula.inputStream())
                    it.setProperty("eula", "true")
                }.store(
//                        "EULA has been auto-agreed by MinecraftServerDeployTools,
                    eula.outputStream(),
                    """
                            EULA has been auto-agreed by MinecraftServerDeployTools
                            the user has been agree Minecraft EULA by our application on 
                        """.trimIndent() + Date().toInstant().atZone(
                        ZoneId.systemDefault()
                    ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                printf("EULA已同意")
                true
            } else {
                false
            }
        } else {
            printf("您已同意EULA，无需再次同意")
            true
        }
    }

    private fun runServer() {
        val runCommand =
            "${tempSettings["JRELocation"]} ${tempSettings["FirstArgs"]} -Xmx${tempSettings["MaxRAM"]}M -Xms${tempSettings["MinRAM"]}M -jar ${
                File(
                    tempSettings["CoreFileSavedDir"],
                    tempSettings["CoreFileName"]!!
                ).absolutePath
            } nogui ${tempSettings["LastArgs"]}"
        printf("正在以「$runCommand」为命令开启服务器，服务端类型为${tempSettings["CoreName"]}")
        ConsoleUtil.terminal.close()
        exitProcess(
            ProcessBuilder(
                sequenceOf(tempSettings["JRELocation"]!!).plus(
                    ConsoleUtil.splitArguments(tempSettings["FirstArgs"]!!)
                ).plus(
                    sequenceOf(
                        "-Xmx${tempSettings["MaxRAM"]}M",
                        "-Xms${tempSettings["MinRAM"]}M",
                        "-jar",
                        File(
                            tempSettings["CoreFileSavedDir"],
                            tempSettings["CoreFileName"]!!
                        ).absolutePath,
                        "nogui"
                    ).plus(ConsoleUtil.splitArguments(tempSettings["LastArgs"]!!))
                ).toList()
            ).directory(File(tempSettings["CoreFileSavedDir"]!!)).inheritIO().start().waitFor()
                .also { println("服务端已关闭") }
        )
    }

    private fun saveSettings() {
        if (!settings.parentFile.exists()) settings.parentFile.mkdir()
        if (!settings.exists()) settings.createNewFile()
        Yaml().dump(tempSettings, settings.outputStream().writer())
    }

    private fun loadSettings() {
        tempSettings = Yaml().load(settings.inputStream())
    }

    private fun commonRun() {
        loadSettings()
        if (agreeEULA()) {
            runServer()
        } else {
            printf("EULA未同意，退出进程...")
            exitProcess(0)
        }
    }
}