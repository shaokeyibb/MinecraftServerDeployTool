package kim.minecraft.minecraftserverdeploytool

import kim.minecraft.minecraftserverdeploytool.guide.RunningManage
import kim.minecraft.minecraftserverdeploytool.ui.MainUI

object Main {

    const val version = "2.0-SNAPSHOT"

    @JvmStatic
    fun main(vararg args: String) {
        if (args.isNotEmpty() && args[0] == "nogui") RunningManage.init() else MainUI.run()
    }
}