package kim.minecraft.minecraftserverdeploytool.tasks

class ArclightDeploy(
    override val saveDir: String,
    override val fileName: String?

) : GitHubDeploy {
    override val author: String = "IzzelAliz"
    override val repo: String = "Arclight"
    override val name: String = "Arclight"
    override val index: Int? = 0
}