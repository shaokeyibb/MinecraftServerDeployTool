package kim.minecraft.minecraftserverdeploytool.tasks

class TuinityDeploy(
    override val buildID: String?,
    override val saveDir: String,
    override val fileName: String?
) : JenkinsCIDeploy {
    override val baseLink: String = "https://ci.codemc.io"
    override val author: String = "Spottedleaf"
    override val repo: String = "Tuinity"
    override val name: String = "Tuinity"
    override val index: Int? = null

}