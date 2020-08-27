package kim.minecraft.minecraftserverdeploytool

import kim.minecraft.minecraftserverdeploytool.guide.FirstRun

object Main {
    @JvmStatic
    fun main(vararg args: String) {
        FirstRun().initWithTry()
    }
}