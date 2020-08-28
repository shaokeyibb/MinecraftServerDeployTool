package kim.minecraft.minecraftserverdeploytool.tasks

class ArclightDeploy(
    val version: String,
    override val saveDir: String,
    override val fileName: String?

) : GitHubDeploy {
    override val author: String = "IzzelAliz"
    override val repo: String = "Arclight"
    override val name: String = "Arclight-$version"
    override val index: Int? = if (version == "1.14.4") 0 else if (version == "1.15.2") 1 else null
}