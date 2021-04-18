package kim.minecraft.minecraftserverdeploytool.tasks

class AkarinDeploy(
    val version: String,
    override val buildID: String?,
    override val saveDir: String,
    override val fileName: String?
) : JenkinsCIDeploy {
    override val baseLink: String = "http://ci.josephworks.net/"
    override val author: String = "Akarin"
    override val repo: String = version
    override val name: String = "Akarin-$version"

    override val index: Int? = null

}
