package kim.minecraft.minecraftserverdeploytool.guide

import kim.minecraft.minecraftserverdeploytool.tasks.*
import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil
import kim.minecraft.minecraftserverdeploytool.utils.PaperMCUtil
import java.io.File
import java.util.*

object RunningManage {

    private val settings: File = File("MCSDTSettings", "settings.yml")

    private val scanner = Scanner(System.`in`)

    private val tempSettings = mutableMapOf<String, String>()

    private fun checkIfFirstRun(): Boolean = !settings.exists()

    fun initWithTry() {
        try {
            init()
        } catch (e: Exception) {
            e.printStackTrace()
            println("服务端部署失败，因为" + e.localizedMessage)
        }
    }

    private fun init() = if (checkIfFirstRun()) firstRun() else commonRun()

    private fun firstRun() {
        println("检测到第一次运行，启动初始化引导程序......")
        Thread.sleep(2000)
        println("Hey，欢迎使用MinecraftServerDeployTools，通过本程序，您可以一键快速构建主流服务端环境并立刻开服!")
        Thread.sleep(2000)
        selectServerCore()
        Thread.sleep(2000)
        deployServerCore()
    }

    private fun selectServerCore() {
        print("可部署核心列表: ")
        ServersManage.Servers.values().forEach { print("${it.name}  ") }
        print("\n接下来，请选择您希望部署的服务端核心名称: ")
        val coreName = scanner.nextLine()
        if (coreName !in ServersManage.Servers.values().groupBy { it.name }) {
            println("输入有误，请检查大小写后重试，或指定服务端不支持部署")
            selectServerCore()
            return
        }
        tempSettings["CoreName"] = coreName
        println("您即将部署服务端 $coreName")
    }

    private fun deployServerCore() {
        when (tempSettings["Corename"]) {
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
                tempSettings["CoreFileName"] = AkarinDeploy(version, build, saveDir, fileName).runTask()
            }
            "Arclight" -> {
                val normalVersion = "1.15.2"
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载 $normalVersion 版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() } ?: normalVersion
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = ArclightDeploy(version, saveDir, fileName).runTask()
            }
            "CatServer" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = CatServerDeploy(saveDir, fileName).runTask()
            }
            "Contigo" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = ContigoDeploy(saveDir, fileName).runTask()
            }
            "CraftBukkit" -> {
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = CraftBukkitDeploy(version, saveDir, fileName).runTask()
            }
            "Magma" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = MagmaDeploy(saveDir, fileName).runTask()
            }
            "Mohist" -> {
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = MohistDeploy(saveDir, fileName).runTask()
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
                tempSettings["CoreFileName"] = PaperDeploy(version, build, saveDir, fileName, fastMode).runTask()
            }
            "Spigot" -> {
                print("请输入您欲下载的服务端核心游戏版本，留空即为下载最新版本: ")
                val version = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = SpigotDeploy(version, saveDir, fileName).runTask()
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
                tempSettings["CoreFileName"] =
                    SpongeForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask()
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
                tempSettings["CoreFileName"] =
                    SpongeVanillaDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask()
            }
            "Tuinity" -> {
                print("请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                val build = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = TuinityDeploy(build, saveDir, fileName).runTask()
            }
            "Uranium" -> {
                print("请输入您欲下载的服务端核心构建版本，留空即为下载最新构建:  ")
                val build = scanner.nextLine().takeIf { it.isNotEmpty() }
                print("请输入您欲部署到的目录，留空即为当前目录: ")
                val saveDir = scanner.nextLine().takeIf { it.isNotEmpty() } ?: ".\\"
                print("请输入您欲部署的服务端核心保存文件名，留空即为原名称: ")
                val fileName = scanner.nextLine().takeIf { it.isNotEmpty() }
                tempSettings["CoreFileName"] = UraniumDeploy(build, saveDir, fileName).runTask()
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
                tempSettings["CoreFileName"] = VanillaDeploy(version, saveDir, fileName, fastMode).runTask()
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
                tempSettings["CoreFileName"] =
                    VanillaForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask()
            }
        }
    }

    private fun commonRun() {

    }
}