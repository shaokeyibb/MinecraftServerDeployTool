package kim.minecraft.minecraftserverdeploytool.tasks

import java.lang.Exception

interface Task {

    fun run()

    fun runTask() {
        try {
            val currentTime = System.currentTimeMillis()
            run()
            println("服务端部署完成，用时" + (System.currentTimeMillis() - currentTime).toDouble() / 1000 + "秒")
        } catch (e: Exception) {
            e.printStackTrace()
            println("服务端部署失败，因为" + e.localizedMessage)
        }
    }

    fun runTaskAsync() {
        Thread {
            runTask()
        }.start()
    }
}