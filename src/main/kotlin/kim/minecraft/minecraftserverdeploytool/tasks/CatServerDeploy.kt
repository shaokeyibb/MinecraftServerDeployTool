package kim.minecraft.minecraftserverdeploytool.tasks

class CatServerDeploy(
    override val saveDir: String,
    override val fileName: String?

) : GitHubDeploy {
    override val author: String = "Luohuayu"
    override val repo: String = "CatServer"
    override val name: String = "CatServer"
    override val index: Int? = 0
}