package kim.minecraft.minecraftserverdeploytool.tasks

class SpigotDeploy(
    override val version: String?,
    override val saveDir: String,
    override val fileName: String?
) : ServerJarsDeploy {
    override val type: String = "spigot"
    override val name: String = "Spigot"
}
