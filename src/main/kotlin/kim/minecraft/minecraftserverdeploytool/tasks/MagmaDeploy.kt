package kim.minecraft.minecraftserverdeploytool.tasks

class MagmaDeploy(
    override val saveDir: String,
    override val fileName: String?

) : GithubDeploy {
    override val author: String = "magmafoundation"
    override val repo: String = "Magma"
    override val name: String = "Magma"
    override val index: Int? = 0
}