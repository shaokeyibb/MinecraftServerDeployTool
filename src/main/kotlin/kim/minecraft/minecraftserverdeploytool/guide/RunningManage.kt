package kim.minecraft.minecraftserverdeploytool.guide

import kim.minecraft.minecraftserverdeploytool.tasks.*
import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil
import kim.minecraft.minecraftserverdeploytool.utils.IOUtil
import kim.minecraft.minecraftserverdeploytool.utils.PaperMCUtil
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.charset.Charset
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.system.exitProcess

object RunningManage {

    private val broadcast: String =
        IOUtil.doSimpleGet("https://raw.githubusercontent.com/shaokeyibb/MinecraftServerDeployTool/master/broadcast")

    private val settings: File = File("MCSDTSettings", "settings.yml")

    private val scanner = Scanner(System.`in`)

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

    fun init() {
        println(broadcast)
        if (checkIfFirstRun()) firstRunWithTry() else commonRun()
    }

    private fun firstRun() {
        println("检测到第一次运行，启动初始化引导程序......")
        Thread.sleep(2000)
        println("Hey，欢迎使用MinecraftServerDeployTools，通过本程序，您可以一键快速构建主流服务端环境并立刻开服!")
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
        print("可部署核心列表: ")
        val cores = ServersManage.Servers.values().toList()
            .also { it.forEachIndexed { index, servers -> print("[${index + 1}]${servers.name} ") } }
        print("\n请选择您希望部署的服务端核心名称: ")
        val coreName = scanner.nextLine()
        val indexed = coreName.toIntOrNull()?.minus(1)
        if ((coreName !in cores.groupBy { it.name }) && (indexed == null || indexed > ServersManage.Servers.values().size)) {
            println("输入有误，请检查大小写后重试，或指定服务端不支持部署")
            selectServerCore()
            return
        }
        if (indexed != null) {
            tempSettings["CoreName"] = cores[indexed].name
        } else {
            tempSettings["CoreName"] = ServersManage.Servers.valueOf(coreName).name
        }

        println("您即将部署服务端 ${tempSettings["CoreName"]}")
    }

    private fun deployServerCore() {
        when (tempSettings["CoreName"]) {
            "Akarin" -> {
                val normalVersion = "1.16.2"
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() } ?: normalVersion
                print("请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                val build = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                AkarinDeploy(version, build, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }

            }
            "Arclight" -> {
                val normalVersion = "1.15.2"
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() } ?: normalVersion
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                ArclightDeploy(version, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "CatServer" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                CatServerDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Contigo" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                ContigoDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "CraftBukkit" -> {
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                CraftBukkitDeploy(version, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Magma" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                MagmaDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Mohist" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                MohistDeploy(saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Paper" -> {
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() }
                PaperMCUtil(PaperMCUtil.Project.PAPER).also { paperMCUtil ->
                    print("可用构建版本列表: ")
                    paperMCUtil.getBuildIDs(version ?: paperMCUtil.latestMinecraftVersion).forEach {
                        print("$it ")
                    }
                }
                print("\n请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                val build = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                println("是否使用BMCLAPI/MCBBS镜像源下载资源文件，是请填写mcbbs或bmclapi，否请输入no，留空使用MCBBS镜像源: ")
                val fastModeRaw = scanner.nextLine()
                val fastMode = if (fastModeRaw.equals(
                        "mcbbs",
                        true
                    ) || fastModeRaw.isEmpty()
                ) BMCLAPIUtil.Src.MCBBS else if (fastModeRaw.equals("bmclapi", true)) BMCLAPIUtil.Src.ORIGINAL else null
                PaperDeploy(version, build, saveDir, fileName, fastMode).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Spigot" -> {
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                SpigotDeploy(version, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "SpongeForge" -> {
                val normalVersion = "1.12.2"
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() } ?: normalVersion
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请选择使用的镜像源（MCBBS/BMCLAPI），留空使用MCBBS镜像源: ")
                val fastModeRaw = scanner.nextLine()
                val fastMode = if (fastModeRaw.equals(
                        "mcbbs",
                        true
                    ) || fastModeRaw.isEmpty()
                ) BMCLAPIUtil.Src.MCBBS else BMCLAPIUtil.Src.ORIGINAL
                print("请输入您欲部署的服务端资源临时目录，留空则使用默认名称: ")
                val cacheDir = scanner.nextLine().takeIf { it.isNotEmpty() }
                SpongeForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "SpongeVanillaDeploy" -> {
                val normalVersion = "1.12.2"
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() } ?: normalVersion
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请选择使用的镜像源（MCBBS/BMCLAPI），留空使用MCBBS镜像源: ")
                val fastModeRaw = scanner.nextLine()
                val fastMode = if (fastModeRaw.equals(
                        "mcbbs",
                        true
                    ) || fastModeRaw.isEmpty()
                ) BMCLAPIUtil.Src.MCBBS else BMCLAPIUtil.Src.ORIGINAL
                print("请输入您欲部署的服务端资源临时目录，留空则使用默认名称: ")
                val cacheDir = scanner.nextLine().takeIf { it.isNotEmpty() }
                SpongeVanillaDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Tuinity" -> {
                print("请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                val build = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                TuinityDeploy(build, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Uranium" -> {
                print("请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                val build = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                UraniumDeploy(build, saveDir, fileName).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "Vanilla" -> {
                val normalVersion = "1.16.2"
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() } ?: normalVersion
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请选择使用的镜像源（MCBBS/BMCLAPI），留空使用MCBBS镜像源: ")
                val fastModeRaw = scanner.nextLine()
                val fastMode = if (fastModeRaw.equals(
                        "mcbbs",
                        true
                    ) || fastModeRaw.isEmpty()
                ) BMCLAPIUtil.Src.MCBBS else BMCLAPIUtil.Src.ORIGINAL
                VanillaDeploy(version, saveDir, fileName, fastMode).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            "VanillaForge" -> {
                val normalVersion = "1.16.2"
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() } ?: normalVersion
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请选择使用的镜像源（MCBBS/BMCLAPI），留空使用MCBBS镜像源: ")
                val fastModeRaw = scanner.nextLine()
                val fastMode = if (fastModeRaw.equals(
                        "mcbbs",
                        true
                    ) || fastModeRaw.isEmpty()
                ) BMCLAPIUtil.Src.MCBBS else BMCLAPIUtil.Src.ORIGINAL
                print("请输入您欲部署的服务端资源临时目录，留空则使用默认名称: ")
                val cacheDir = scanner.nextLine().takeIf { it.isNotEmpty() }
                VanillaForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    tempSettings["CoreFileName"] = it.name
                    tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: ".\\"
                }
            }
            else -> {
                throw Exception("未知的服务端名称: ${tempSettings["Corename"]}")
            }
        }
    }

    private fun setRuntimeArgs() {
        println("接下来，请遵循向导配置您的服务端运行参数")
        print("请输入您希望使用的Java运行时环境文件(如java.exe)位置，留空则使用「Java」作为运行时文件位置: ")
        val jreLocation = scanner.nextLine().takeIf { it.isNotEmpty() } ?: "Java"
        tempSettings["JRELocation"] = jreLocation
        println("参数已被设置为「$jreLocation」")
        print("请输入您希望在开服时分配的最大内存，单位MB，留空则使用1024MB: ")
        val maxRAM = scanner.nextLine().takeIf { it.isNotEmpty() && it.toIntOrNull() != null } ?: "1024"
        tempSettings["MaxRAM"] = maxRAM
        println("参数已被设置为「$maxRAM」")
        print("请输入您希望在开服时分配的最小内存，单位MB，留空则与最大内存对等: ")
        val minRAM = scanner.nextLine().takeIf { it.isNotEmpty() && it.toIntOrNull() != null } ?: maxRAM
        tempSettings["MinRAM"] = minRAM
        println("参数已被设置为「$minRAM」")
        print("请输入您希望加载的Java额外命令行前置参数(JVM参数)，留空则不使用额外参数: ")
        val firstArgs = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ""
        tempSettings["FirstArgs"] = firstArgs
        println("参数已被设置为「$firstArgs」")
        print("请输入您希望加载的Java额外命令行后置参数(服务端参数)，留空则不使用额外参数: ")
        val lastArgs = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ""
        println("参数已被设置为「$lastArgs」")
        tempSettings["LastArgs"] = lastArgs
        println("运行参数设置完毕，保存配置中")
        saveSettings()
        println("配置文件已保存，您随时可在 ${settings.canonicalPath} 位置找到此配置文件")
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
            println("开服前，您需要同意Minecraft软件用户最终许可协议(EULA)，您可前往 https://account.mojang.com/documents/minecraft_eula 获取EULA全文")
            print("如果您同意EULA，请输入「agree」: ")
            if (scanner.nextLine() == "agree") {
                if (!eula.exists()) eula.createNewFile()
                Properties().also {
                    it.load(eula.inputStream())
                    it.setProperty("eula", "true")
                }.store(
                    eula.outputStream(),
                    "EULA has been auto-agreed by MinecraftServerDeployTools,the user has been agree Minecraft EULA by our application on ${
                        Date().toInstant().atZone(
                            ZoneId.systemDefault()
                        ).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    }"
                )
                println("EULA已同意")
                true
            } else {
                false
            }
        } else {
            println("您已同意EULA，无需再次同意")
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
            } -nogui ${tempSettings["LastArgs"]}"
        println("正在以「$runCommand」为命令开启服务器，服务端类型为${tempSettings["CoreName"]}")
        Runtime.getRuntime().exec(
            runCommand, null, File(tempSettings["CoreFileSavedDir"]!!)
        ).also { process ->
            Runtime.getRuntime().addShutdownHook(Thread {
                process.destroyForcibly()
            })
            Thread {
                process.inputStream.bufferedReader(Charset.defaultCharset()).lines().forEach(::println)
            }.start()
            Thread {
                process.inputStream.bufferedReader(Charset.defaultCharset()).lines().forEach(::println)
            }.start()
            process.outputStream.bufferedWriter().also {
                while (scanner.hasNext()) {
                    it.write(scanner.nextLine())
                    it.newLine()
                    it.flush()
                }
                it.close()
            }
        }
        println("服务端已关闭")
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
            println("EULA未同意，退出进程...")
            exitProcess(0)
        }
    }
}