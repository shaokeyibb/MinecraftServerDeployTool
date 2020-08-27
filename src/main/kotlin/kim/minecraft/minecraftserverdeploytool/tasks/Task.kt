package kim.minecraft.minecraftserverdeploytool.tasks

interface Task {

    fun run(): String

    fun runTask(): String {
        val currentTime = System.currentTimeMillis()
        val ret = run()
        println("服务端部署完成，用时" + (System.currentTimeMillis() - currentTime).toDouble() / 1000 + "秒")
        return ret
    }

    fun runTaskAsync(): String {
        lateinit var ret: String
        Thread {
            ret = runTask()
        }.start()
        return ret
    }
}