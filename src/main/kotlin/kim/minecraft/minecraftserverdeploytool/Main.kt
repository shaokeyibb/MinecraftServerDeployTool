package kim.minecraft.minecraftserverdeploytool

import kim.minecraft.minecraftserverdeploytool.guide.RunningManage

object Main {

    const val version = "1.3.2-SNAPSHOT"

    @JvmStatic
    fun main(vararg args: String) {
        RunningManage.init()
    }
}