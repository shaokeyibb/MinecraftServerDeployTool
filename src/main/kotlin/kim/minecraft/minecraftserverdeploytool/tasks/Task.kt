package kim.minecraft.minecraftserverdeploytool.tasks

import java.io.File

interface Task {

    fun run(): File

    fun runTask(): File {
        val currentTime = System.currentTimeMillis()
        val ret = run()
        println("服务端部署完成，用时" + (System.currentTimeMillis() - currentTime).toDouble() / 1000 + "秒")
        return ret
    }

    fun runTaskAsync(): File {
        lateinit var ret: File
        Thread {
            ret = runTask()
        }.start()
        return ret
    }
}