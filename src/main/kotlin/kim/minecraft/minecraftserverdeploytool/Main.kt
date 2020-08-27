package kim.minecraft.minecraftserverdeploytool

import kim.minecraft.minecraftserverdeploytool.guide.RunningManage

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        RunningManage.initWithTry()
    }
}