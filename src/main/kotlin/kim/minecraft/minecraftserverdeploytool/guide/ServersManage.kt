package kim.minecraft.minecraftserverdeploytool.guide

import kim.minecraft.minecraftserverdeploytool.tasks.Task

object ServersManage {

    enum class Servers {
        Akarin,
        Arclight,
        CatServer,
        Contigo,
        CraftBukkit,
        Magma,
        Mohist,
        Paper,
        Spigot,
        SpongeForge,
        SpongeVanilla,
        Tuinity,
        Uranium,
        Vanilla,
        VanillaForge
    }

    @Deprecated(message = "参数不同步导致无法使用反射",level = DeprecationLevel.HIDDEN)
    fun deploy(servers: Servers, isAsync: Boolean, vararg args: String?) {
        val con = Class.forName("kim.minecraft.minecraftserverdeploytool.tasks.${servers.name}Deploy")
            .constructors[0]
        if (con.parameterCount == 2) {
            val arg0 = args[0]
            val arg1 = args[1]
            val instance = con.newInstance(arg0, arg1) as Task
            if (isAsync) instance.runTaskAsync() else instance.runTask()
        } else if (con.parameterCount == 3) {
            val arg0 = args[0]
            val arg1 = args[1]
            val arg2 = args[2]
            val instance = con.newInstance(arg0, arg1, arg2) as Task
            if (isAsync) instance.runTaskAsync() else instance.runTask()
        } else if (con.parameterCount == 4) {
            val arg0 = args[0]
            val arg1 = args[1]
            val arg2 = args[2]
            val arg3 = args[3]
            val instance = con.newInstance(arg0, arg1, arg2, arg3) as Task
            if (isAsync) instance.runTaskAsync() else instance.runTask()
        }else if (con.parameterCount==5){
            val arg0 = args[0]
            val arg1 = args[1]
            val arg2 = args[2]
            val arg3 = args[3]
            val arg4 = args[4]
            val instance = con.newInstance(arg0, arg1, arg2, arg3,arg4) as Task
            if (isAsync) instance.runTaskAsync() else instance.runTask()
        }else throw Exception("缺少参数声明")
    }
}