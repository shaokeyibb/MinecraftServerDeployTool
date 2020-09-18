package kim.minecraft.minecraftserverdeploytool.tasks

class CraftBukkitDeploy(
    override val version: String?,
    override val saveDir: String,
    override val fileName: String?
) : ServerJarsDeploy {
    override val type: String = "bukkit"
    override val name: String = "CraftBukkit"
}
