package kim.minecraft.minecraftserverdeploytool

import kim.minecraft.minecraftserverdeploytool.guide.RunningManage

object Main {

    const val version = "1.0-SNAPSHOT"

    @JvmStatic
    fun main(vararg args: String) {
        RunningManage.init()
    }
}