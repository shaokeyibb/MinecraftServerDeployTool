package kim.minecraft.minecraftserverdeploytool.tasks

class MohistDeploy(
    override val saveDir: String,
    override val fileName: String?

) : GitHubDeploy {
    override val author: String = "Mohist-Community"
    override val repo: String = "Mohist"
    override val name: String = "Mohist"
    override val index: Int? = 0
}