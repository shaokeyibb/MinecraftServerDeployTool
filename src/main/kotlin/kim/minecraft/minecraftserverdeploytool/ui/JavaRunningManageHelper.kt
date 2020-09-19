package kim.minecraft.minecraftserverdeploytool.ui

import kim.minecraft.minecraftserverdeploytool.tasks.*
import kim.minecraft.minecraftserverdeploytool.utils.BMCLAPIUtil
import kim.minecraft.minecraftserverdeploytool.utils.PaperMCUtil
import javax.swing.JOptionPane

object JavaRunningManageHelper {
    fun deployServerCore(instance: RunningManage, core: String) {
        when (core) {
            "Akarin" -> {
                val normalVersion = "1.16.2"
                val version = instance.askForServerCoreVersion(normalVersion)
                val build = instance.askForServerCoreBuildVersion()
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                AkarinDeploy(version, build, saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }

            }
            "Arclight" -> {
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                ArclightDeploy(saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "CatServer" -> {
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                CatServerDeploy(saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Contigo" -> {
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                ContigoDeploy(saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "CraftBukkit" -> {
                val version = instance.askForServerCoreVersion()
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                CraftBukkitDeploy(version, saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Magma" -> {
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                MagmaDeploy(saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Mohist" -> {
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                MohistDeploy(saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Paper" -> {
                val paper = PaperMCUtil(PaperMCUtil.Project.PAPER)
                val versions = paper.getAllMinecraftVersions()
                val version = instance.askForServerCoreVersion(versions.map { it.toString() })
                val buildIDs = paper.let { paperMCUtil ->
                    paperMCUtil.getBuildIDs(version ?: paperMCUtil.latestMinecraftVersion)
                }
                val build = instance.askForServerCoreBuildVersion(buildIDs.map { it.toString() })
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                val fastMode = when (instance.askForDoUseBMCLAPIWithNo()) {
                    "mcbbs", "" -> BMCLAPIUtil.Src.MCBBS
                    "bmclapi" -> BMCLAPIUtil.Src.ORIGINAL
                    else -> null
                }
                PaperDeploy(version, build, saveDir, fileName, fastMode).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Spigot" -> {
                val version = instance.askForServerCoreVersion()
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                SpigotDeploy(version, saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "SpongeForge" -> {
                val normalVersion = "1.12.2"
                val version = instance.askForServerCoreVersion(normalVersion)
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                val fastModeRaw = instance.askForDoUseBMCLAPI()
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = instance.askForCacheDir()
                SpongeForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "SpongeVanilla" -> {
                val normalVersion = "1.12.2"
                val version = instance.askForServerCoreVersion(normalVersion)
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                val fastModeRaw = instance.askForDoUseBMCLAPI()
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = instance.askForCacheDir()
                SpongeVanillaDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Tuinity" -> {
                val build = instance.askForServerCoreBuildVersion()
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                TuinityDeploy(build, saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Uranium" -> {
                val build = instance.askForServerCoreBuildVersion()
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                UraniumDeploy(build, saveDir, fileName).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "Vanilla" -> {
                val normalVersion = "1.16.2"
                val version = instance.askForServerCoreVersion(normalVersion)
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                val fastModeRaw = instance.askForDoUseBMCLAPI()
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                VanillaDeploy(version, saveDir, fileName, fastMode).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "VanillaFabric" -> {
                val normalVersion = "1.16.2"
                val version = instance.askForServerCoreVersion(normalVersion)
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                val fastModeRaw = instance.askForDoUseBMCLAPI()
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = instance.askForCacheDir()
                VanillaFabricDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            "VanillaForge" -> {
                val normalVersion = "1.16.2"
                val version = instance.askForServerCoreVersion(normalVersion)
                val saveDir = instance.askForSaveDir()
                val fileName = instance.askForFileName()
                val fastModeRaw = instance.askForDoUseBMCLAPI()
                val fastMode = if (fastModeRaw == "bmclapi") BMCLAPIUtil.Src.ORIGINAL else BMCLAPIUtil.Src.MCBBS
                val cacheDir = instance.askForCacheDir()
                VanillaForgeDeploy(version, saveDir, fileName, fastMode, cacheDir).runTask().also {
                    instance.tempSettings["CoreFileName"] = it.name
                    instance.tempSettings["CoreFileSavedDir"] = it.parentFile.absolutePath ?: "./"
                }
            }
            else -> {
                throw Exception("未知的服务端名称: ${instance.tempSettings["Corename"]}")
            }
        }
    }

    fun setRuntimeArgs(instance: RunningManage, frame: MainUI.MainFrame) {
        val jreLocation =
            JOptionPane.showInputDialog(frame.startLabel, "请输入您希望使用的Java运行时环境文件(如java.exe)位置，留空则使用「Java」作为运行时文件位置: ")
                .takeIf { it.isNotEmpty() } ?: "Java"
        instance.tempSettings["JRELocation"] = jreLocation
        val maxRAM = JOptionPane.showInputDialog(frame.startLabel, "请输入您希望在开服时分配的最大内存，单位MB，留空则使用1024MB: ")
            .takeIf { it.isNotEmpty() && it.toIntOrNull() != null } ?: "1024"
        instance.tempSettings["MaxRAM"] = maxRAM
        val minRAM = JOptionPane.showInputDialog(frame.startLabel, "请输入您希望在开服时分配的最小内存，单位MB，留空则与最大内存对等: ")
            .takeIf { it.isNotEmpty() && it.toIntOrNull() != null } ?: maxRAM
        instance.tempSettings["MinRAM"] = minRAM
        val firstArgs = JOptionPane.showInputDialog(frame.startLabel, "请输入您希望加载的Java额外命令行前置参数(JVM参数)，留空则不使用额外参数: ")
        instance.tempSettings["FirstArgs"] = firstArgs
        val lastArgs = JOptionPane.showInputDialog(frame.startLabel, "请输入您希望加载的Java额外命令行后置参数(服务端参数)，留空则不使用额外参数: ")
            .takeIf { it.isNotEmpty() } ?: ""
        instance.tempSettings["LastArgs"] = lastArgs
        instance.saveSettings()
        JOptionPane.showConfirmDialog(
            frame.startLabel,
            "配置文件已保存\n您随时可在\n ${instance.settings.canonicalPath} \n找到此配置文件",
            null,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        )
    }
}